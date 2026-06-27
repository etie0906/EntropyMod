package org.EntropyMod.entropymod.challenges;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Random;

public class MovementSpeedChallenge implements Challenge {
    private static final Identifier SPEED_MODIFIER_ID = Identifier.of("entropymod", "movement_speed_bonus");

    private boolean active = false;
    private double speedBonus = 0;
    private int ticksUntilNextIncrease = 0;
    private boolean firstPhase = false;
    private final Random random = new Random();

    @Override
    public String getId() { return "movement_speed"; }

    @Override
    public String getName() { return "Movement Speed"; }

    @Override
    public Text getDescription() {
        return Text.literal("Randomly changes your movement speed over time");
    }

    @Override
    public void start(MinecraftServer server, List<ServerPlayerEntity> players) {
        active = true;
        firstPhase = true;
        speedBonus = 0;
        ticksUntilNextIncrease = 6000;

        for (ServerPlayerEntity player : players) {
            player.sendMessage(Text.literal("§aMovement Speed Challenge gestartet! In 5 Minuten wirst du langsamer."), false);
        }
    }

    @Override
    public void stop(MinecraftServer server) {
        active = false;
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                removeModifier(player);
                player.sendMessage(Text.literal("§cMovement Speed Challenge gestoppt."), false);
            }
        }
    }

    @Override
    public void pause(MinecraftServer server) {
        active = false;
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                removeModifier(player);
            }
        }
    }

    @Override
    public void resume(MinecraftServer server) {
        active = true;
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                applyModifier(player);
            }
        }
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!active || server == null) return;

        ticksUntilNextIncrease--;
        if (ticksUntilNextIncrease <= 0) {
            double change;
            if (firstPhase) {
                change = -0.1 + 0.05 * random.nextDouble();
                firstPhase = false;
            } else {
                change = 0.005 + 0.095 * random.nextDouble();
            }
            speedBonus += change;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                applyModifier(player);

                EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                double currentSpeed = attr != null ? attr.getValue() : 0;

                if (firstPhase) {
                    player.sendMessage(Text.literal(String.format("§eBewegungstempo verringert um %.3f! Aktuell: %.3f", -change, currentSpeed)), false);
                } else {
                    player.sendMessage(Text.literal(String.format("§eBewegungstempo erhöht um %.3f! Aktuell: %.3f", change, currentSpeed)), false);
                }
            }

            ticksUntilNextIncrease = 6000 + random.nextInt(12001);
        }
    }

    @Override
    public boolean isActive() { return active; }

    @Override
    public void setActive(boolean active) { this.active = active; }

    public void skipWait() {
        ticksUntilNextIncrease = 0;
    }

    private void applyModifier(ServerPlayerEntity player) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (attr == null) return;

        attr.removeModifier(SPEED_MODIFIER_ID);

        EntityAttributeModifier modifier = new EntityAttributeModifier(
                SPEED_MODIFIER_ID, speedBonus, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        attr.addTemporaryModifier(modifier);
    }

    private void removeModifier(ServerPlayerEntity player) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(SPEED_MODIFIER_ID);
        }
    }
}
