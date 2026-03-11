package org.EntropyMod.entropymod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.EntropyMod.entropymod.Entropymod;
import org.EntropyMod.entropymod.client.menu.screens.MainLobbyScreen;
import org.EntropyMod.entropymod.client.timer.TimerOverlay;
import org.EntropyMod.entropymod.network.ChallengePackets;
import org.lwjgl.glfw.GLFW;

public class EntropymodClient implements ClientModInitializer {
    public static KeyBinding openMenuKey;

    public static final KeyBinding.Category ENTROPYMOD_CATEGORY =
            KeyBinding.Category.create(Identifier.of(Entropymod.MOD_ID, "general"));

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.entropymod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                ENTROPYMOD_CATEGORY
        ));

        // Tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMenuKey.wasPressed() && client.player != null) {
                client.setScreen(new MainLobbyScreen());
            }
        });

        registerPacketHandlers();
    }

    private void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ChallengePackets.TimerUpdatePayload.ID,
                (payload, context) -> {
                    String time = payload.time();
                    String color = payload.color();
                    String state = payload.state();

                    context.client().execute(() -> {
                        TimerOverlay.update(time, color, state);
                        if (context.client().currentScreen instanceof MainLobbyScreen screen) {
                            screen.updateTimer(time, color, state);
                        }
                    });
                });

        ClientPlayNetworking.registerGlobalReceiver(ChallengePackets.ChallengeStatePayload.ID,
                (payload, context) -> {
                    String challengeId = payload.challengeId();
                    boolean active = payload.active();

                    context.client().execute(() -> {
                        if (context.client().currentScreen instanceof MainLobbyScreen screen) {
                            screen.updateChallengeState(challengeId, active);
                        }
                    });
                });
    }
}