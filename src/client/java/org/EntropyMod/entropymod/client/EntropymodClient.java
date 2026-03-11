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

    // FIX: KeyBinding category must be a KeyBinding.Category, not a raw String.
    // Use KeyBinding.Category.create() to register a custom category.
    public static final KeyBinding.Category ENTROPYMOD_CATEGORY =
            KeyBinding.Category.create("category.entropymod.general", 100);

    @Override
    public void onInitializeClient() {
        // Register keybinding
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

        // Register packet receivers
        registerPacketHandlers();
    }

    private void registerPacketHandlers() {
        // FIX: ChallengePackets.TIMER_UPDATE and CHALLENGE_STATE must be declared
        // as CustomPayload IDs in ChallengePackets. The receiver API also changed in
        // 1.21 — use the typed payload receiver pattern.
        ClientPlayNetworking.registerGlobalReceiver(ChallengePackets.TIMER_UPDATE_ID,
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

        ClientPlayNetworking.registerGlobalReceiver(ChallengePackets.CHALLENGE_STATE_ID,
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