package org.EntropyMod.entropymod.challenges;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public interface Challenge {
    String getId();
    String getName();
    Text getDescription();
    void start(MinecraftServer server, List<ServerPlayerEntity> players);
    void stop(MinecraftServer server);
    void pause(MinecraftServer server);
    void resume(MinecraftServer server);
    void tick(MinecraftServer server);
    boolean isActive();
    void setActive(boolean active);
}
