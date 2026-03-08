package org.EntropyMod.entropymod.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.EntropyMod.entropymod.Entropymod;

public class ChallengePackets {
    public static final Identifier TIMER_UPDATE = new Identifier(Entropymod.MOD_ID, "timer_update");
    public static final Identifier CHALLENGE_STATE = new Identifier(Entropymod.MOD_ID, "challenge_state");
    public static final Identifier OPEN_MENU = new Identifier(Entropymod.MOD_ID, "open_menu");

    public static void register() {
        // Client-to-server packets
        ServerPlayNetworking.registerGlobalReceiver(OPEN_MENU, (server, player, handler, buf, responseSender) -> {
            // Handle menu open request
        });
    }

    public static void sendTimerUpdate(ServerPlayerEntity player, String time, String color, String state) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(time);
        buf.writeString(color);
        buf.writeString(state);
        ServerPlayNetworking.send(player, TIMER_UPDATE, buf);
    }

    public static void sendChallengeState(ServerPlayerEntity player, String challengeId, boolean active) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(challengeId);
        buf.writeBoolean(active);
        ServerPlayNetworking.send(player, CHALLENGE_STATE, buf);
    }
}
