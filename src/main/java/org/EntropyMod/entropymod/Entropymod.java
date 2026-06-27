package org.EntropyMod.entropymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.EntropyMod.entropymod.challenges.ChallengeManager;
import org.EntropyMod.entropymod.challenges.RandomItemChallenge;
import org.EntropyMod.entropymod.commands.ChallengesCommand;
import org.EntropyMod.entropymod.commands.TimerCommand;
import org.EntropyMod.entropymod.freezer.WorldFreezer;
import org.EntropyMod.entropymod.network.ChallengePackets;
import org.EntropyMod.entropymod.timer.TimerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entropymod implements ModInitializer {
    public static final String MOD_ID = "entropymod";
    public static final String MOD_VERSION = "a0.1.114t";
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
            ChallengeManager.getInstance().tick(server);
        });

        // Player Events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            TimerManager.getInstance().onPlayerJoin(handler.player);
            WorldFreezer.getInstance().onPlayerJoin(handler.player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            TimerManager.getInstance().onPlayerLeave(handler.player);
        });

        // Respawn handling (for frozen players)
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            WorldFreezer.getInstance().onPlayerRespawn(newPlayer);
        });

        // RandomItemChallenge Events (einmalig registriert — kein Duplikat bei Neustart)
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (!ChallengeManager.getInstance().isChallengeActive("random_item")) return true;
            if (!(player instanceof ServerPlayerEntity)) return true;

            Block block = state.getBlock();
            Item replacement = RandomItemChallenge.getBlockDrop(block);
            if (replacement != null && !world.isClient()) {
                world.breakBlock(pos, false);
                world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);
                ItemStack drop = new ItemStack(replacement, 1);
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                return false;
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!ChallengeManager.getInstance().isChallengeActive("random_item")) return;
            if (entity.getEntityWorld().isClient()) return;

            net.minecraft.server.world.ServerWorld world = (net.minecraft.server.world.ServerWorld) entity.getEntityWorld();
            Box box = entity.getBoundingBox().expand(5.0);
            java.util.List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, box, e -> true);

            for (ItemEntity itemEntity : items) {
                ItemStack stack = itemEntity.getStack();
                Item originalItem = stack.getItem();
                Item replacement = RandomItemChallenge.getItemReplacement(originalItem);

                if (replacement != null) {
                    int count = stack.getCount();
                    ItemStack newStack = new ItemStack(replacement, count);
                    ItemEntity newEntity = new ItemEntity(world,
                            itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), newStack);
                    newEntity.setVelocity(itemEntity.getVelocity());
                    itemEntity.discard();
                    world.spawnEntity(newEntity);
                }
            }
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
        WorldFreezer.getInstance().freeze();

        LOGGER.info("Server started with {} players", server.getPlayerManager().getPlayerList().size());
    }

    private void onServerStopping(MinecraftServer server) {
        WorldFreezer.getInstance().unfreeze();
    }
}