package org.EntropyMod.entropymod.challenges;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class DummyChallenge implements Challenge {
    private boolean active = false;
    private int tickCount = 0;

    @Override
    public String getId() { return "dummy"; }

    @Override
    public String getName() { return "Dummy Challenge"; }

    @Override
    public Text getDescription() {
        return Text.literal("A test challenge that does nothing. Use this to verify the menu system works.")
                .formatted(Formatting.GRAY);
    }

    @Override
    public void start(MinecraftServer server, List<ServerPlayerEntity> players) {
        active = true;
        tickCount = 0;
        broadcast(server, "Dummy Challenge started! (This is a test)");
    }

    @Override
    public void stop(MinecraftServer server) {
        active = false;
        broadcast(server, "Dummy Challenge stopped!");
    }

    @Override
    public void pause(MinecraftServer server) {
        broadcast(server, "Dummy Challenge paused!");
    }

    @Override
    public void resume(MinecraftServer server) {
        broadcast(server, "Dummy Challenge resumed!");
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!active) return;

        tickCount++;
        // Every 30 seconds, send a test message
        if (tickCount % 600 == 0) {
            broadcast(server, "Dummy Challenge is running... (" + (tickCount / 20) + "s)");
        }
    }

    @Override
    public boolean isActive() { return active; }

    @Override
    public void setActive(boolean active) { this.active = active; }

    private void broadcast(MinecraftServer server, String msg) {
        if (server != null) {
            server.getPlayerManager().broadcast(
                    Text.literal("[Dummy] ").formatted(Formatting.GOLD)
                            .append(Text.literal(msg).formatted(Formatting.WHITE)),
                    false
            );
        }
    }
}
