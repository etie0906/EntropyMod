package org.EntropyMod.entropymod.client.menu.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GoalRaceScreen extends Screen {
    private final Screen parent;

    public GoalRaceScreen(Screen parent) {
        super(Text.literal("Goal / Race Mode"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 60;

        this.addDrawableChild(new net.minecraft.client.gui.widget.TextWidget(
                centerX - 100, 30, 200, 20,
                Text.literal("Select Race Goal").formatted(Formatting.GOLD, Formatting.BOLD),
                this.textRenderer
        ));

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("🌲 Collect All Wood Types"),
                this::selectWoodRace
        ).dimensions(centerX - 100, y, 200, 20).build());

        y += 30;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("⚔ Kill All Bosses"),
                this::selectBossRace
        ).dimensions(centerX - 100, y, 200, 20).build());

        y += 30;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("🏆 Most Achievements"),
                this::selectAchievementRace
        ).dimensions(centerX - 100, y, 200, 20).build());

        y += 30;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("⛏ Block Collection"),
                this::selectBlockRace
        ).dimensions(centerX - 100, y, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back").formatted(Formatting.DARK_RED),
                this::goBack
        ).dimensions(centerX - 50, this.height - 40, 100, 20).build());
    }

    private void selectWoodRace(ButtonWidget button)        { startRace("wood"); }
    private void selectBossRace(ButtonWidget button)        { startRace("boss"); }
    private void selectAchievementRace(ButtonWidget button) { startRace("achievement"); }
    private void selectBlockRace(ButtonWidget button)       { startRace("block"); }

    private void startRace(String type) {
        if (this.client != null && this.client.player != null) {
            this.client.player.networkHandler.sendChatCommand("race start " + type);
        }
        goBack(null);
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