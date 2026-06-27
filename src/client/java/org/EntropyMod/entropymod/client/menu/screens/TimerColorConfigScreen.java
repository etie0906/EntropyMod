package org.EntropyMod.entropymod.client.menu.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TimerColorConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget colorField;

    public TimerColorConfigScreen(Screen parent) {
        super(Text.literal("Timer Colors"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 60;

        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, 30, 200, 20,
                Text.literal("Timer Color Configuration").formatted(Formatting.GOLD, Formatting.BOLD),
                this.textRenderer
        ));

        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, y, 200, 20,
                Text.literal("Timer Color (name or #hex)"),
                this.textRenderer
        ));

        colorField = new TextFieldWidget(this.textRenderer, centerX - 100, y + 20, 200, 20,
                Text.literal("WHITE"));
        colorField.setText("WHITE");
        this.addDrawableChild(colorField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save").formatted(Formatting.GREEN),
                this::saveColor
        ).dimensions(centerX - 105, this.height - 60, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back").formatted(Formatting.RED),
                this::goBack
        ).dimensions(centerX + 5, this.height - 60, 100, 20).build());
    }

    private void saveColor(ButtonWidget button) {
        if (this.client != null && this.client.player != null) {
            this.client.player.networkHandler.sendChatCommand(
                    "timer color " + colorField.getText());
        }
        goBack(button);
    }

    private void goBack(ButtonWidget button) {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xBB0A0A1A);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
