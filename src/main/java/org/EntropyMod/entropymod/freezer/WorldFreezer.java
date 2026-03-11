package org.EntropyMod.entropymod.freezer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.rule.GameRules;
import org.EntropyMod.entropymod.Entropymod;

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
            // KORRIGIERT: GameRules.DO_DAYLIGHT_CYCLE als Key verwenden
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, server);

            for (Entity entity : world.getEntitiesByClass(Entity.class,
                    new net.minecraft.util.math.Box(
                            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
                    ), e -> !(e instanceof PlayerEntity))) {

                if (entity instanceof LivingEntity living) {
                    // KORRIGIERT: setAiDisabled() statt setNoAi()
                    living.setAiDisabled(true);
                }

                // KORRIGIERT: getPos() -> getBlockPos() oder direct field access
                frozenPositions.put(entity.getUuid(), entity.getPos());
                frozenVelocities.put(entity.getUuid(), entity.getVelocity());
                entity.setVelocity(Vec3d.ZERO);
                entity.velocityDirty = true;
            }
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            frozenPositions.put(player.getUuid(), player.getPos());
            frozenVelocities.put(player.getVelocity());
            player.setVelocity(Vec3d.ZERO);
            player.velocityDirty = true;
        }
    }

    public void unfreeze() {
        if (!frozen || server == null) return;

        frozen = false;

        for (ServerWorld world : server.getWorlds()) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, server);

            for (Entity entity : world.getEntitiesByClass(Entity.class,
                    new net.minecraft.util.math.Box(
                            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
                    ), e -> !(e instanceof PlayerEntity))) {

                if (entity instanceof LivingEntity living) {
                    living.setAiDisabled(false);
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
                // KORRIGIERT: Einfacher Teleport ohne PositionFlag
                player.teleport(
                        player.getServerWorld(),
                        frozenPos.x, frozenPos.y, frozenPos.z,
                        java.util.EnumSet.noneOf(net.minecraft.network.packet.s2c.play.PositionFlag.class),
                        player.getYaw(), player.getPitch(),
                        true
                );
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