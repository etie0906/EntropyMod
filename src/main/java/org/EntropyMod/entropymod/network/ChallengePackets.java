package org.EntropyMod.entropymod.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.EntropyMod.entropymod.Entropymod;

public class ChallengePackets {

    // Timer Update Payload
    public record TimerUpdatePayload(String time, String color, String state) implements CustomPayload {
        public static final Id<TimerUpdatePayload> ID = new Id<>(Identifier.of(Entropymod.MOD_ID, "timer_update"));
        public static final PacketCodec<PacketByteBuf, TimerUpdatePayload> CODEC = PacketCodec.of(
                (payload, buf) -> {
                    buf.writeString(payload.time);
                    buf.writeString(payload.color);
                    buf.writeString(payload.state);
                },
                buf -> new TimerUpdatePayload(buf.readString(), buf.readString(), buf.readString())
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    // Challenge State Payload
    public record ChallengeStatePayload(String challengeId, boolean active) implements CustomPayload {
        public static final Id<ChallengeStatePayload> ID = new Id<>(Identifier.of(Entropymod.MOD_ID, "challenge_state"));
        public static final PacketCodec<PacketByteBuf, ChallengeStatePayload> CODEC = PacketCodec.of(
                (payload, buf) -> {
                    buf.writeString(payload.challengeId);
                    buf.writeBoolean(payload.active);
                },
                buf -> new ChallengeStatePayload(buf.readString(), buf.readBoolean())
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(TimerUpdatePayload.ID, TimerUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ChallengeStatePayload.ID, ChallengeStatePayload.CODEC);
    }

    public static void sendTimerUpdate(ServerPlayerEntity player, String time, String color, String state) {
        ServerPlayNetworking.send(player, new TimerUpdatePayload(time, color, state));
    }

    public static void sendChallengeState(ServerPlayerEntity player, String challengeId, boolean active) {
        ServerPlayNetworking.send(player, new ChallengeStatePayload(challengeId, active));
    }
}