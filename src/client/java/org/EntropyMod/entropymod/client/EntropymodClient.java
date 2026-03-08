package org.EntropyMod.entropymod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.EntropyMod.entropymod.client.menu.screens.MainLobbyScreen;
import org.EntropyMod.entropymod.client.timer.TimerOverlay;
import org.EntropyMod.entropymod.network.ChallengePackets;
import org.lwjgl.glfw.GLFW;

public class EntropymodClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        // Register keybinding
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.entropymod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.entropymod.general"
        ));

        // Tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMenuKey.wasPressed() && client.player != null) {
                client.setScreen(new MainLobbyScreen());
            }
        });

        // Register packet receivers
        registerPacketHandlers();
    }

    private void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ChallengePackets.TIMER_UPDATE, (client, handler, buf, responseSender) -> {
            String time = buf.readString();
            String color = buf.readString();
            String state = buf.readString();

            client.execute(() -> {
                TimerOverlay.update(time, color, state);
                // Also update if menu is open
                if (client.currentScreen instanceof MainLobbyScreen screen) {
                    screen.updateTimer(time, color, state);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ChallengePackets.CHALLENGE_STATE, (client, handler, buf, responseSender) -> {
            String challengeId = buf.readString();
            boolean active = buf.readBoolean();

            client.execute(() -> {
                // Update menu if open
                if (client.currentScreen instanceof MainLobbyScreen screen) {
                    screen.updateChallengeState(challengeId, active);
                }
            });
        });
    }
}