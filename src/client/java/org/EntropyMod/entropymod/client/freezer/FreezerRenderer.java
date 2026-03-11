package org.EntropyMod.entropymod.client.freezer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FreezerRenderer implements HudRenderCallback {
    private static boolean frozen = false;

    public static void init() {
        HudRenderCallback.EVENT.register(new FreezerRenderer());
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!frozen) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int x = client.getWindow().getScaledWidth() / 2;
        int y = 30;

        Text frozenText = Text.literal("⏸ WORLD FROZEN").formatted(Formatting.AQUA, Formatting.BOLD);

        drawContext.drawCenteredTextWithShadow(client.textRenderer, frozenText, x, y, 0x00FFFF);
    }

    public static void setFrozen(boolean frozen) {
        FreezerRenderer.frozen = frozen;
    }
}