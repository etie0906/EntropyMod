package org.EntropyMod.entropymod.client.menu.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChallengeSelectScreen extends Screen {
    private final Screen parent;

    public ChallengeSelectScreen(Screen parent) {
        super(Text.literal("Select Challenge"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Title
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, 30, 200, 20,
                Text.literal("Available Challenges").formatted(Formatting.GOLD, Formatting.BOLD),
                this.textRenderer
        ));

        // Dummy Challenge Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Dummy Challenge"),
                this::selectDummy
        ).dimensions(centerX - 100, centerY - 20, 200, 20).build());

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back").formatted(Formatting.RED),
                this::goBack
        ).dimensions(centerX - 50, this.height - 40, 100, 20).build());
    }

    private void selectDummy(ButtonWidget button) {
        // Send to server: start dummy challenge
        if (this.client != null && this.client.player != null) {
            this.client.player.networkHandler.sendChatCommand("challenge start dummy");
        }
        this.goBack(button);
    }

    private void goBack(ButtonWidget button) {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
