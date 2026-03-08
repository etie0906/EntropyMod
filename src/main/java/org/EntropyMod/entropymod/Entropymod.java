package org.EntropyMod.entropymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.EntropyMod.entropymod.challenges.ChallengeManager;
import org.EntropyMod.entropymod.commands.ChallengesCommand;
import org.EntropyMod.entropymod.commands.TimerCommand;
import org.EntropyMod.entropymod.freezer.WorldFreezer;
import org.EntropyMod.entropymod.network.ChallengePackets;
import org.EntropyMod.entropymod.race.RaceManager;
import org.EntropyMod.entropymod.timer.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entropymod implements ModInitializer {
    public static final String MOD_ID = "entropymod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing EntropyMod Challenges System");

        // Register packets
        ChallengePackets.register();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ChallengesCommand.register(dispatcher);
            TimerCommand.register(dispatcher);
        });

        // Server tick events
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            TimerManager.getInstance().tick(server);
            ChallengeManager.getInstance().tick(server);
            RaceManager.getInstance().tick(server);
        });

        // Player join/leave handling
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            TimerManager.getInstance().onPlayerJoin(handler.player);
            ChallengeManager.getInstance().onPlayerJoin(handler.player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            TimerManager.getInstance().onPlayerLeave(handler.player);
            // Pause timer if no players left
            if (server.getPlayerCount() <= 1) {
                TimerManager.getInstance().pause();
                WorldFreezer.getInstance().setFrozen(true);
            }
        });

        // Server stop - save data
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            TimerManager.getInstance().save();
            ChallengeManager.getInstance().save();
        });

        // Server start - load data
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            TimerManager.getInstance().load(server);
            ChallengeManager.getInstance().load(server);
            WorldFreezer.getInstance().init(server);
        });
    }
}