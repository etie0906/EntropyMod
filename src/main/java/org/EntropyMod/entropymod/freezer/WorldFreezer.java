package org.EntropyMod.entropymod.freezer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.rule.GameRules;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldFreezer {
    private static WorldFreezer instance;
    private boolean frozen = false;
    private MinecraftServer server;
    private long frozenTime;
    private Map<UUID, Vec3d> frozenPositions = new HashMap<>();
    private Map<UUID, Vec3d> frozenVelocities = new HashMap<>();

    public static WorldFreezer getInstance() {
        if (instance == null) {
            instance = new WorldFreezer();
        }
        return instance;
    }

    public void init(MinecraftServer server) {
        this.server = server;
    }

    public void freeze() {
        if (frozen || server == null) return;

        frozen = true;
        frozenTime = server.getOverworld().getTime();

        for (ServerWorld world : server.getWorlds()) {
            world.getGameRules().setValue(GameRules.ADVANCE_TIME, false, server);

            for (Entity entity : world.getEntitiesByClass(Entity.class,
                    new net.minecraft.util.math.Box(
                            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
                    ), e -> !(e instanceof PlayerEntity))) {

                if (entity instanceof MobEntity mob) {
                    mob.setAiDisabled(true);
                }
                frozenPositions.put(entity.getUuid(), new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
                frozenVelocities.put(entity.getUuid(), entity.getVelocity());
                entity.setVelocity(Vec3d.ZERO);
                entity.velocityDirty = true;
            }
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            frozenPositions.put(player.getUuid(), new Vec3d(player.getX(), player.getY(), player.getZ()));
            frozenVelocities.put(player.getUuid(), player.getVelocity());
            player.setVelocity(Vec3d.ZERO);
            player.velocityDirty = true;
        }
    }

    public void unfreeze() {
        if (!frozen || server == null) return;

        frozen = false;

        for (ServerWorld world : server.getWorlds()) {
            world.getGameRules().setValue(GameRules.ADVANCE_TIME, true, server);

            for (Entity entity : world.getEntitiesByClass(Entity.class,
                    new net.minecraft.util.math.Box(
                            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
                    ), e -> !(e instanceof PlayerEntity))) {

                if (entity instanceof MobEntity mob) {
                    mob.setAiDisabled(false);
                }

                Vec3d vel = frozenVelocities.get(entity.getUuid());
                if (vel != null) {
                    entity.setVelocity(vel);
                    entity.velocityDirty = true;
                }
            }
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Vec3d vel = frozenVelocities.get(player.getUuid());
            if (vel != null) {
                player.setVelocity(vel);
                player.velocityDirty = true;
            }
        }

        frozenPositions.clear();
        frozenVelocities.clear();
    }

    public void tick() {
        if (!frozen || server == null) return;

        for (ServerWorld world : server.getWorlds()) {
            world.setTimeOfDay(frozenTime);
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Vec3d frozenPos = frozenPositions.get(player.getUuid());
            if (frozenPos != null) {
                player.requestTeleport(frozenPos.x, frozenPos.y, frozenPos.z);
                player.setVelocity(Vec3d.ZERO);
                player.velocityDirty = true;
            }
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (frozen) {
            player.setVelocity(Vec3d.ZERO);
            player.velocityDirty = true;
        }
    }
}