package org.EntropyMod.entropymod.freezer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldFreezer {
    private static WorldFreezer instance;
    private boolean frozen = false;
    private MinecraftServer server;
    private Map<UUID, Vec3d> frozenPositions = new HashMap<>();

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

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            frozenPositions.put(player.getUuid(), new Vec3d(player.getX(), player.getY(), player.getZ()));
            player.changeGameMode(GameMode.ADVENTURE);
            player.setVelocity(Vec3d.ZERO);
            player.velocityDirty = true;
        }
    }

    public void unfreeze() {
        if (!frozen || server == null) return;

        frozen = false;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Vec3d pos = frozenPositions.get(player.getUuid());
            if (pos != null) {
                player.requestTeleport(pos.x, pos.y, pos.z);
            }
            player.changeGameMode(GameMode.SURVIVAL);
            Vec3d vel = Vec3d.ZERO;
            player.setVelocity(vel);
            player.velocityDirty = true;
        }

        frozenPositions.clear();
    }

    public void tick() {
        if (!frozen || server == null) return;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Vec3d frozenPos = frozenPositions.get(player.getUuid());
            if (frozenPos != null) {
                if (player.getGameMode() != GameMode.ADVENTURE) {
                    player.changeGameMode(GameMode.ADVENTURE);
                }
                player.requestTeleport(frozenPos.x, frozenPos.y, frozenPos.z);
                player.setVelocity(Vec3d.ZERO);
                player.velocityDirty = true;
            } else {
                frozenPositions.put(player.getUuid(), new Vec3d(player.getX(), player.getY(), player.getZ()));
                player.changeGameMode(GameMode.ADVENTURE);
            }
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (frozen) {
            frozenPositions.put(player.getUuid(), new Vec3d(player.getX(), player.getY(), player.getZ()));
            player.changeGameMode(GameMode.ADVENTURE);
            player.setVelocity(Vec3d.ZERO);
            player.velocityDirty = true;
        }
    }

    public void onPlayerRespawn(ServerPlayerEntity newPlayer) {
        if (frozen) {
            Vec3d pos = frozenPositions.get(newPlayer.getUuid());
            if (pos != null) {
                newPlayer.requestTeleport(pos.x, pos.y, pos.z);
            }
            newPlayer.changeGameMode(GameMode.ADVENTURE);
        }
    }

    public boolean isFrozen() { return frozen; }
}
