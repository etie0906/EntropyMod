package org.EntropyMod.entropymod.freezer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.EntropyMod.entropymod.Entropymod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldFreezer {
    private static WorldFreezer instance;

    private boolean frozen = true;
    private MinecraftServer server;
    private Map<UUID, Vec3d> frozenPositions = new HashMap<>();
    private Map<UUID, Vec3d> frozenVelocities = new HashMap<>();
    private long frozenTime = 0;

    private WorldFreezer() {}

    public static WorldFreezer getInstance() {
        if (instance == null) instance = new WorldFreezer();
        return instance;
    }

    public void init(MinecraftServer server) {
        this.server = server;
        this.frozen = true; // Start frozen until timer starts
    }

    public void setFrozen(boolean frozen) {
        if (this.frozen == frozen) return;

        this.frozen = frozen;

        if (frozen) {
            freezeAll();
        } else {
            unfreezeAll();
        }
    }

    private void freezeAll() {
        if (server == null) return;

        frozenTime = server.getOverworld().getTime();

        for (ServerWorld world : server.getWorlds()) {
            // Freeze time
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, server);

            // Freeze entities
            for (Entity entity : world.getEntitiesByClass(Entity.class,
                    new net.minecraft.util.math.Box(
                            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
                    ), e -> !(e instanceof PlayerEntity))) {

                if (entity instanceof LivingEntity living) {
                    living.setAiDisabled(true);
                }
                entity.setVelocity(Vec3d.ZERO);
                entity.velocityModified = true;
            }
        }

        // Freeze players
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            frozenPositions.put(player.getUuid(), player.getPos());
            frozenVelocities.put(player.getUuid(), player.getVelocity());
            player.setVelocity(Vec3d.ZERO);
            player.velocityModified = true;
        }
    }

    private void unfreezeAll() {
        if (server == null) return;

        for (ServerWorld world : server.getWorlds()) {
            // Unfreeze time
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, server);

            // Unfreeze entities
            for (Entity entity : world.getEntitiesByClass(Entity.class,
                    new net.minecraft.util.math.Box(
                            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
                    ), e -> !(e instanceof PlayerEntity))) {

                if (entity instanceof LivingEntity living) {
                    living.setAiDisabled(false);
                }
            }
        }

        frozenPositions.clear();
        frozenVelocities.clear();
    }

    public void tick(MinecraftServer server) {
        if (!frozen || server == null) return;

        // Keep time frozen
        for (ServerWorld world : server.getWorlds()) {
            world.setTimeOfDay(frozenTime);
        }

        // Keep players frozen
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Vec3d frozenPos = frozenPositions.get(player.getUuid());
            if (frozenPos != null) {
                // Teleport back if moved
                if (player.squaredDistanceTo(frozenPos) > 0.01) {
                    player.teleport(frozenPos.x, frozenPos.y, frozenPos.z);
                }
                player.setVelocity(Vec3d.ZERO);
                player.velocityModified = true;
            }
        }
    }

    public boolean isFrozen() { return frozen; }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (frozen) {
            frozenPositions.put(player.getUuid(), player.getPos());
            player.setVelocity(Vec3d.ZERO);
        }
    }
}
