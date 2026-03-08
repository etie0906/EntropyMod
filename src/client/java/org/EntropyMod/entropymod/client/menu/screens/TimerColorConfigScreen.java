package org.EntropyMod.entropymod.client.menu.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TimerColorConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget runningColorField;
    private TextFieldWidget pausedColorField;
    private TextFieldWidget lowTimeColorField;

    public TimerColorConfigScreen(Screen parent) {
        super(Text.literal("Timer Colors"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 60;

        // Title
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, 30, 200, 20,
                Text.literal("Timer Color Configuration").formatted(Formatting.GOLD, Formatting.BOLD),
                this.textRenderer
        ));

        // Running Color
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, y, 200, 20,
                Text.literal("Running Color (green/#00FF00)"),
                this.textRenderer
        ));

        runningColorField = new TextFieldWidget(this.textRenderer, centerX - 100, y + 20, 200, 20,
                Text.literal("green"));
        runningColorField.setText("green");
        this.addDrawableChild(runningColorField);

        y += 50;

        // Paused Color
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, y, 200, 20,
                Text.literal("Paused Color (yellow/#FFFF00)"),
                this.textRenderer
        ));

        pausedColorField = new TextFieldWidget(this.textRenderer, centerX - 100, y + 20, 200, 20,
                Text.literal("yellow"));
        pausedColorField.setText("yellow");
        this.addDrawableChild(pausedColorField);

        y += 50;

        // Low Time Color
        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, y, 200, 20,
                Text.literal("Low Time Color (red/#FF0000)"),
                this.textRenderer
        ));

        lowTimeColorField = new TextFieldWidget(this.textRenderer, centerX - 100, y + 20, 200, 20,
                Text.literal("red"));
        lowTimeColorField.setText("red");
        this.addDrawableChild(lowTimeColorField);

        // Save Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Save").formatted(Formatting.GREEN),
                this::saveColors
        ).dimensions(centerX - 105, this.height - 60, 100, 20).build());

        // Back Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back").formatted(Formatting.RED),
                this::goBack
        ).dimensions(centerX + 5, this.height - 60, 100, 20).build());
    }

    private void saveColors(ButtonWidget button) {
        // Send color commands
        if (this.client != null && this.client.player != null) {
            this.client.player.networkHandler.sendChatCommand(
                    "timer color running " + runningColorField.getText());
            this.client.player.networkHandler.sendChatCommand(
                    "timer color paused " + pausedColorField.getText());
            this.client.player.networkHandler.sendChatCommand(
                    "timer color low " + lowTimeColorField.getText());
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
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
