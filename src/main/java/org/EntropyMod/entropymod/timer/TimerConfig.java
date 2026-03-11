package org.EntropyMod.entropymod.timer;

import net.minecraft.util.Identifier;
import org.EntropyMod.entropymod.Entropymod;

public class TimerConfig {
    public static final Identifier TIMER_CONFIG_ID = Identifier.of(Entropymod.MOD_ID, "timer_config");
    private int days = 0;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private boolean upwards = true;

    // Colors (Minecraft color codes or hex)
    private String runningColor = "green";
    private String pausedColor = "yellow";
    private String lowTimeColor = "red";
    private int lowTimeThreshold = 60; // seconds

    public int getTotalSeconds() {
        return days * 86400 + hours * 3600 + minutes * 60 + seconds;
    }

    public void setFromTotalSeconds(int total) {
        days = total / 86400;
        total %= 86400;
        hours = total / 3600;
        total %= 3600;
        minutes = total / 60;
        seconds = total % 60;
    }

    // Getters and Setters
    public int getDays() { return days; }
    public void setDays(int days) { this.days = Math.max(0, days); }
    public void addDays(int delta) { this.days = Math.max(0, this.days + delta); }

    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = Math.max(0, Math.min(23, hours)); }
    public void addHours(int delta) {
        this.hours += delta;
        if (this.hours > 23) {
            this.days += this.hours / 24;
            this.hours %= 24;
        } else if (this.hours < 0) {
            this.days = Math.max(0, this.days - 1);
            this.hours = 23;
        }
    }

    public int getMinutes() { return minutes; }
    public void setMinutes(int minutes) { this.minutes = Math.max(0, Math.min(59, minutes)); }
    public void addMinutes(int delta) {
        this.minutes += delta;
        if (this.minutes > 59) {
            addHours(this.minutes / 60);
            this.minutes %= 60;
        } else if (this.minutes < 0) {
            addHours(-1);
            this.minutes = 59;
        }
    }

    public int getSeconds() { return seconds; }
    public void setSeconds(int seconds) { this.seconds = Math.max(0, Math.min(59, seconds)); }
    public void addSeconds(int delta) {
        this.seconds += delta;
        if (this.seconds > 59) {
            addMinutes(this.seconds / 60);
            this.seconds %= 60;
        } else if (this.seconds < 0) {
            addMinutes(-1);
            this.seconds = 59;
        }
    }

    public boolean isUpwards() { return upwards; }
    public void setUpwards(boolean upwards) { this.upwards = upwards; }

    public String getRunningColor() { return runningColor; }
    public void setRunningColor(String color) { this.runningColor = color; }
    public String getPausedColor() { return pausedColor; }
    public void setPausedColor(String color) { this.pausedColor = color; }
    public String getLowTimeColor() { return lowTimeColor; }
    public void setLowTimeColor(String color) { this.lowTimeColor = color; }
    public int getLowTimeThreshold() { return lowTimeThreshold; }
    public void setLowTimeThreshold(int threshold) { this.lowTimeThreshold = threshold; }

    public String formatTime() {
        if (days > 0) {
            return String.format("%d Day%s %02d:%02d:%02d",
                    days, days == 1 ? "" : "s", hours, minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void reset() {
        days = 0; hours = 0; minutes = 0; seconds = 0;
        upwards = true;
        runningColor = "green";
        pausedColor = "yellow";
        lowTimeColor = "red";
        lowTimeThreshold = 60;
    }
}
