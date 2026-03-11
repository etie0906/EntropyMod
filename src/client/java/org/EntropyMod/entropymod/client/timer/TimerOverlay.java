package org.EntropyMod.entropymod.client.timer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TimerOverlay implements HudRenderCallback {
    private static String currentTime = "00:00:00";
    private static String currentColor = "white";
    private static String currentState = "STOPPED";

    public static void init() {
        HudRenderCallback.EVENT.register(new TimerOverlay());
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (client.currentScreen != null) return;

        int x = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight() - 55;

        Text timeText = formatTime(currentTime, currentColor);

        // Draw background
        int textWidth = client.textRenderer.getWidth(timeText);
        drawContext.fill(x - textWidth / 2 - 5, y - 2, x + textWidth / 2 + 5, y + 12, 0x88000000);

        // Draw time
        drawContext.drawCenteredTextWithShadow(client.textRenderer, timeText, x, y, 0xFFFFFF);
    }

    private Text formatTime(String time, String color) {
        Formatting formatting = Formatting.byName(color.toUpperCase());
        if (formatting == null) {
            if (color.startsWith("#")) {
                try {
                    int hex = Integer.parseInt(color.substring(1), 16);
                    return Text.literal(time).styled(s -> s.withColor(hex));
                } catch (Exception e) {
                    return Text.literal(time);
                }
            }
            return Text.literal(time);
        }
        return Text.literal(time).formatted(formatting);
    }

    public static void update(String time, String color, String state) {
        currentTime = time;
        currentColor = color;
        currentState = state;
    }
}