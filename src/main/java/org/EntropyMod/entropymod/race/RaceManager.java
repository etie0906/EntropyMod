package org.EntropyMod.entropymod.race;

import net.minecraft.server.MinecraftServer;

public class RaceManager {
    private static RaceManager instance;
    private boolean active = false;
    private RaceGoal currentGoal = null;

    private RaceManager() {}

    public static RaceManager getInstance() {
        if (instance == null) instance = new RaceManager();
        return instance;
    }

    public void tick(MinecraftServer server) {
        if (!active || currentGoal == null) return;

        // Check players for goal completion
    }

    public void startRace(RaceGoal goal) {
        this.currentGoal = goal;
        this.active = true;
        goal.onStart();
    }

    public void stopRace() {
        this.active = false;
        if (currentGoal != null) {
            currentGoal.reset();
        }
    }

    public boolean isActive() { return active; }
}
