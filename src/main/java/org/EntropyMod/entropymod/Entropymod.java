package org.EntropyMod.entropymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.EntropyMod.entropymod.commands.ChallengesCommand;
import org.EntropyMod.entropymod.commands.TimerCommand;
import org.EntropyMod.entropymod.freezer.WorldFreezer;
import org.EntropyMod.entropymod.network.ChallengePackets;
import org.EntropyMod.entropymod.timer.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entropymod implements ModInitializer {
    public static final String MOD_ID = "entropymod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing EntropyMod");

        // Networking registrieren
        ChallengePackets.register();

        // Server Lifecycle
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

        // Server Tick
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            TimerManager.getInstance().tick();
            WorldFreezer.getInstance().tick();
        });

        // Player Events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            TimerManager.getInstance().onPlayerJoin(handler.player);
            WorldFreezer.getInstance().onPlayerJoin(handler.player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            TimerManager.getInstance().onPlayerLeave(handler.player);
        });

        // Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ChallengesCommand.register(dispatcher);
            TimerCommand.register(dispatcher);
        });
    }

    private void onServerStarted(MinecraftServer server) {
        TimerManager.getInstance().init(server);
        WorldFreezer.getInstance().init(server);

        LOGGER.info("Server started with {} players", server.getPlayerManager().getPlayerList().size());
    }

    private void onServerStopping(MinecraftServer server) {
        WorldFreezer.getInstance().unfreeze();
    }
}