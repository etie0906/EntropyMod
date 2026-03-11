package org.EntropyMod.entropymod.timer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.rule.GameRules;
import org.EntropyMod.entropymod.Entropymod;
import org.EntropyMod.entropymod.network.ChallengePackets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerManager {
    private static TimerManager instance;
    private MinecraftServer server;
    private boolean running = false;
    private boolean upwards = true;
    private int time = 0;
    private String color = "WHITE";
    private Map<UUID, Boolean> frozenPlayers = new HashMap<>();

    // ENTFERNE: import org.w3c.dom.Text; (war die falsche Text-Klasse)

    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    public void init(MinecraftServer server) {
        this.server = server;
    }

    public void tick() {
        if (!running || server == null) return;

        if (upwards) {
            time++;
        } else {
            if (time > 0) time--;
        }

        broadcastTime();
    }

    private void broadcastTime() {
        if (server == null) return;

        String timeStr = formatTime(time);
        Text timerText = formatColoredText(timeStr, color);

        // KORRIGIERT: getPlayerManager() -> getPlayerManager()
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(timerText, true);
            ChallengePackets.sendTimerUpdate(player, timeStr, color, running ? "running" : "paused");
        }
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    // KORRIGIERT: Rückgabetyp ist net.minecraft.text.Text (nicht org.w3c.dom.Text)
    private Text formatColoredText(String text, String color) {
        try {
            if (color.startsWith("#")) {
                int hex = Integer.parseInt(color.substring(1), 16);
                return Text.literal(text).styled(s -> s.withColor(hex));
            } else {
                return Text.literal(text).formatted(Formatting.WHITE);
            }
        } catch (Exception e) {
            Formatting formatting = Formatting.byName(color.toUpperCase());
            if (formatting == null) formatting = Formatting.WHITE;
            return Text.literal(text).formatted(formatting);
        }
    }

    public void start() { running = true; }
    public void pause() { running = false; }
    public void stop() { running = false; time = 0; }
    public void setUpwards(boolean up) { this.upwards = up; }
    public void setColor(String color) { this.color = color; }
    public void setTime(int seconds) { this.time = seconds; }

    public void onPlayerJoin(ServerPlayerEntity player) {
        frozenPlayers.put(player.getUuid(), false);
    }

    public void onPlayerLeave(ServerPlayerEntity player) {
        frozenPlayers.remove(player.getUuid());
    }

    // KORRIGIERT: getPlayerManager() statt getPlayerManager()
    private void broadcastMessage(String msg) {
        if (server != null) {
            server.getPlayerManager().broadcast(Text.literal("[EntropyMod] " + msg), false);
        }
    }
}