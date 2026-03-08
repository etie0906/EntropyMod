package org.EntropyMod.entropymod.timer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import org.EntropyMod.entropymod.Entropymod;
import org.EntropyMod.entropymod.freezer.WorldFreezer;
import org.EntropyMod.entropymod.network.ChallengePackets;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TimerManager {
    private static TimerManager instance;

    private TimerState state = TimerState.STOPPED;
    private TimerConfig config = new TimerConfig();
    private int currentSeconds = 0;
    private int tickCounter = 0;
    private MinecraftServer server;
    private Set<UUID> playersInMenu = new HashSet<>();

    private TimerManager() {}

    public static TimerManager getInstance() {
        if (instance == null) instance = new TimerManager();
        return instance;
    }

    public void init(MinecraftServer server) {
        this.server = server;
    }

    public void tick(MinecraftServer server) {
        if (this.server == null) this.server = server;

        if (state == TimerState.RUNNING) {
            tickCounter++;
            if (tickCounter >= 20) { // Every second
                tickCounter = 0;
                updateTimer();
            }
        }

        // Send update to all players
        if (tickCounter % 5 == 0) { // Update display 4 times per second
            broadcastTimer();
        }
    }

    private void updateTimer() {
        if (config.isUpwards()) {
            currentSeconds++;
            config.setFromTotalSeconds(currentSeconds);
        } else {
            if (currentSeconds > 0) {
                currentSeconds--;
                config.setFromTotalSeconds(currentSeconds);
                if (currentSeconds == 0) {
                    onTimerEnd();
                }
            }
        }
    }

    private void onTimerEnd() {
        // Timer reached zero in countdown mode
        broadcastMessage("Timer ended!");
        // Could trigger challenge end here
    }

    public void broadcastTimer() {
        if (server == null) return;

        String timeStr = config.formatTime();
        String color = getCurrentColor();
        Text timerText = formatColoredText(timeStr, color);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            // Send to actionbar
            player.sendMessage(timerText, true);

            // Send to menu if open
            if (playersInMenu.contains(player.getUuid())) {
                ChallengePackets.sendTimerUpdate(player, timeStr, color, state.name());
            }
        }
    }

    private String getCurrentColor() {
        if (state == TimerState.PAUSED) return config.getPausedColor();
        if (state == TimerState.STOPPED) return "gray";
        if (!config.isUpwards() && currentSeconds <= config.getLowTimeThreshold()) {
            return config.getLowTimeColor();
        }
        return config.getRunningColor();
    }

    private org.w3c.dom.Text formatColoredText(String text, String color) {
        // Handle hex colors
        if (color.startsWith("#")) {
            try {
                int hex = Integer.parseInt(color.substring(1), 16);
                return Text.literal(text).styled(s -> s.withColor(hex));
            } catch (Exception e) {
                return Text.literal(text).formatted(Formatting.WHITE);
            }
        }

        // Handle named colors
        Formatting formatting = Formatting.byName(color.toUpperCase());
        if (formatting == null) formatting = Formatting.WHITE;
        return Text.literal(text).formatted(formatting);
    }

    public void start() {
        if (state == TimerState.STOPPED) {
            currentSeconds = config.getTotalSeconds();
        }
        state = TimerState.RUNNING;
        WorldFreezer.getInstance().setFrozen(false);
        broadcastMessage("Timer started!");
    }

    public void pause() {
        if (state == TimerState.RUNNING) {
            state = TimerState.PAUSED;
            WorldFreezer.getInstance().setFrozen(true);
            broadcastMessage("Timer paused!");
        }
    }

    public void resume() {
        if (state == TimerState.PAUSED) {
            state = TimerState.RUNNING;
            WorldFreezer.getInstance().setFrozen(false);
            broadcastMessage("Timer resumed!");
        }
    }

    public void stop() {
        state = TimerState.STOPPED;
        currentSeconds = config.getTotalSeconds();
        WorldFreezer.getInstance().setFrozen(true);
        broadcastMessage("Timer stopped!");
    }

    public void setTime(int days, int hours, int minutes, int seconds) {
        config.setDays(days);
        config.setHours(hours);
        config.setMinutes(minutes);
        config.setSeconds(seconds);
        if (state == TimerState.STOPPED) {
            currentSeconds = config.getTotalSeconds();
        }
    }

    public void setUpwards(boolean upwards) {
        config.setUpwards(upwards);
    }

    public void setColor(String type, String color) {
        switch (type.toLowerCase()) {
            case "running" -> config.setRunningColor(color);
            case "paused" -> config.setPausedColor(color);
            case "low" -> config.setLowTimeColor(color);
        }
    }

    public void resetSettings() {
        config.reset();
        if (state == TimerState.STOPPED) {
            currentSeconds = 0;
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        // Send current state
        ChallengePackets.sendTimerUpdate(player, config.formatTime(), getCurrentColor(), state.name());
    }

    public void onPlayerLeave(ServerPlayerEntity player) {
        playersInMenu.remove(player.getUuid());
    }

    public void setPlayerInMenu(UUID playerUuid, boolean inMenu) {
        if (inMenu) playersInMenu.add(playerUuid);
        else playersInMenu.remove(playerUuid);
    }

    public void broadcastMessage(String msg) {
        if (server != null) {
            server.getPlayerManager().broadcast(Text.literal("[EntropyMod] " + msg), false);
        }
    }

    public TimerState getState() { return state; }
    public TimerConfig getConfig() { return config; }
    public boolean isRunning() { return state == TimerState.RUNNING; }

    public void save() {
        // Save to file
        // Implementation for persistence
    }

    public void load(MinecraftServer server) {
        // Load from file
        this.server = server;
    }
}
