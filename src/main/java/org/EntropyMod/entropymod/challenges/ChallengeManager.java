package org.EntropyMod.entropymod.challenges;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.EntropyMod.entropymod.timer.TimerManager;

import java.util.*;

public class ChallengeManager {
    private static ChallengeManager instance;

    private Map<String, Challenge> availableChallenges = new HashMap<>();
    private List<Challenge> activeChallenges = new ArrayList<>();
    private MinecraftServer server;

    private ChallengeManager() {
        registerDefaultChallenges();
    }

    public static ChallengeManager getInstance() {
        if (instance == null) instance = new ChallengeManager();
        return instance;
    }

    private void registerDefaultChallenges() {
        registerChallenge(new DummyChallenge());
        registerChallenge(new MovementSpeedChallenge());
        registerChallenge(new RandomItemChallenge());
    }

    public void registerChallenge(Challenge challenge) {
        availableChallenges.put(challenge.getId(), challenge);
    }

    public void tick(MinecraftServer server) {
        if (this.server == null) this.server = server;

        for (Challenge challenge : activeChallenges) {
            if (challenge.isActive()) {
                challenge.tick(server);
            }
        }
    }

    public boolean startChallenge(String id) {
        Challenge challenge = availableChallenges.get(id);
        if (challenge == null) return false;

        if (!challenge.isActive()) {
            for (Challenge c : activeChallenges) {
                if (c.isActive()) {
                    c.stop(server);
                }
            }
            activeChallenges.clear();

            challenge.start(server, getActivePlayers());
            activeChallenges.add(challenge);
            TimerManager.getInstance().start();
            return true;
        }
        return false;
    }

    public boolean stopChallenge(String id) {
        Challenge challenge = availableChallenges.get(id);
        if (challenge != null && challenge.isActive()) {
            challenge.stop(server);
            activeChallenges.remove(challenge);
            TimerManager.getInstance().stop();
            return true;
        }
        return false;
    }

    public void stopAll() {
        for (Challenge challenge : activeChallenges) {
            if (challenge.isActive()) {
                challenge.stop(server);
            }
        }
        activeChallenges.clear();
        TimerManager.getInstance().stop();
    }

    public void pauseAll() {
        for (Challenge challenge : activeChallenges) {
            if (challenge.isActive()) {
                challenge.pause(server);
            }
        }
        TimerManager.getInstance().pause();
    }

    public void resumeAll() {
        for (Challenge challenge : activeChallenges) {
            if (challenge.isActive()) {
                challenge.resume(server);
            }
        }
        TimerManager.getInstance().resume();
    }

    public List<Challenge> getAvailableChallenges() {
        return new ArrayList<>(availableChallenges.values());
    }

    public List<Challenge> getActiveChallenges() {
        return new ArrayList<>(activeChallenges);
    }

    public boolean isChallengeActive(String id) {
        Challenge c = availableChallenges.get(id);
        return c != null && c.isActive();
    }

    public Challenge getChallenge(String id) {
        return availableChallenges.get(id);
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
    }

    private List<ServerPlayerEntity> getActivePlayers() {
        if (server == null) return new ArrayList<>();
        return server.getPlayerManager().getPlayerList();
    }
}
