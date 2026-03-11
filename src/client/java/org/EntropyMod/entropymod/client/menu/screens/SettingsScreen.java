package org.EntropyMod.entropymod.client.menu.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.Difficulty;

public class SettingsScreen extends Screen {
    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(Text.literal("Challenge Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 60;

        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, 30, 200, 20,
                Text.literal("Game Settings").formatted(Formatting.GOLD, Formatting.BOLD),
                this.textRenderer
        ));

        this.addDrawableChild(
                CyclingButtonWidget.<Difficulty>builder(difficulty -> Text.literal(difficulty.getName()), Difficulty.EASY)
                        .values(Difficulty.values())
                        .build(centerX - 100, y, 200, 20, Text.literal("Difficulty"),
                                (btn, value) -> { /* apply difficulty */ })
        );

        y += 30;

        this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(false)
                        .build(centerX - 100, y, 200, 20, Text.literal("Keep Inventory"),
                                (btn, value) -> { /* apply keep inventory */ })
        );

        y += 30;

        this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(false)
                        .build(centerX - 100, y, 200, 20, Text.literal("One Death = All Die"),
                                (btn, value) -> { /* apply one death rule */ })
        );

        y += 30;

        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20,
                Text.literal("Minecart Speed: 1.0x"), 0.5) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Minecart Speed: " + String.format("%.1fx", this.value * 2)));
            }

            @Override
            protected void applyValue() {
                // Apply minecart speed
            }
        });

        y += 40;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Timer Colors"),
                this::openTimerColors
        ).dimensions(centerX - 100, y, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset All").formatted(Formatting.RED),
                this::resetSettings
        ).dimensions(centerX + 5, y, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back").formatted(Formatting.GRAY),
                this::goBack
        ).dimensions(centerX - 50, this.height - 40, 100, 20).build());
    }

    private void openTimerColors(ButtonWidget button) {
        if (this.client != null) this.client.setScreen(new TimerColorConfigScreen(this));
    }

    private void resetSettings(ButtonWidget button) {
        if (this.client != null && this.client.player != null) {
            this.client.player.networkHandler.sendChatCommand("timer reset");
        }
    }

    private void goBack(ButtonWidget button) {
        if (this.client != null) this.client.setScreen(parent);
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