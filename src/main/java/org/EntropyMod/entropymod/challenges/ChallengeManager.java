package org.EntropyMod.entropymod.challenges;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class ChallengeManager {
    private static ChallengeManager instance;

    private Map<String, Challenge> availableChallenges = new HashMap<>();
    private List<Challenge> activeChallenges = new ArrayList<>();
    private Set<UUID> readyPlayers = new HashSet<>();
    private UUID adminUuid = null;
    private boolean forceStarted = false;
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
        // Add more challenges here later
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
            challenge.start(server, getActivePlayers());
            activeChallenges.add(challenge);
            return true;
        }
        return false;
    }

    public boolean stopChallenge(String id) {
        Challenge challenge = availableChallenges.get(id);
        if (challenge != null && challenge.isActive()) {
            challenge.stop(server);
            activeChallenges.remove(challenge);
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
    }

    public void pauseAll() {
        for (Challenge challenge : activeChallenges) {
            if (challenge.isActive()) {
                challenge.pause(server);
            }
        }
    }

    public void resumeAll() {
        for (Challenge challenge : activeChallenges) {
            if (challenge.isActive()) {
                challenge.resume(server);
            }
        }
    }

    public List<Challenge> getAvailableChallenges() {
        return new ArrayList<>(availableChallenges.values());
    }

    public List<Challenge> getActiveChallenges() {
        return new ArrayList<>(activeChallenges);
    }

    // Ready system
    public void setReady(UUID playerUuid, boolean ready) {
        if (ready) readyPlayers.add(playerUuid);
        else readyPlayers.remove(playerUuid);

        checkAllReady();
    }

    public boolean isReady(UUID playerUuid) {
        return readyPlayers.contains(playerUuid);
    }

    public void clearReady() {
        readyPlayers.clear();
    }

    private void checkAllReady() {
        if (server == null) return;

        int playerCount = server.getPlayerManager().getPlayerList().size();
        if (readyPlayers.size() >= playerCount && playerCount > 0 && !forceStarted) {
            // Auto-start when all ready (optional, or wait for admin)
        }
    }

    public void forceStart(UUID adminUuid) {
        this.adminUuid = adminUuid;
        this.forceStarted = true;
        // Start all selected challenges
    }

    public boolean isForceStarted() { return forceStarted; }

    public void setAdmin(UUID uuid) {
        this.adminUuid = uuid;
    }

    public UUID getAdmin() { return adminUuid; }

    public boolean isAdmin(UUID uuid) {
        if (adminUuid == null) return false;
        return adminUuid.equals(uuid);
    }

    private List<ServerPlayerEntity> getActivePlayers() {
        if (server == null) return new ArrayList<>();
        return server.getPlayerManager().getPlayerList();
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        // If no admin, first player becomes admin
        if (adminUuid == null) {
            adminUuid = player.getUuid();
        }
    }

    public void save() {
        // Persist state
    }

    public void load(MinecraftServer server) {
        this.server = server;
    }
}
