package org.EntropyMod.entropymod.client.menu.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.EntropyMod.entropymod.client.menu.widgets.ChallengeListWidget;
import org.EntropyMod.entropymod.client.menu.widgets.DigitalTimerWidget;
import org.EntropyMod.entropymod.client.menu.widgets.PlayerListWidget;

public class MainLobbyScreen extends Screen {
    private PlayerListWidget playerList;
    private ChallengeListWidget challengeList;
    private DigitalTimerWidget timerWidget;
    private ButtonWidget readyButton;
    private ButtonWidget forceStartButton;
    private ButtonWidget challengeMenuButton;
    private ButtonWidget settingsButton;
    private ButtonWidget goalRaceButton;

    private boolean isAdmin = false;
    private boolean isReady = false;

    public MainLobbyScreen() {
        super(Text.literal("Challenge Lobby"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Title
        TextWidget title = new TextWidget(
                centerX - 100, 20, 200, 20,
                Text.literal("EntropyMod Challenges").formatted(Formatting.GOLD, Formatting.BOLD),
                this.textRenderer
        );
        this.addDrawableChild(title);

        // Player List (Left side)
        playerList = new PlayerListWidget(
                this.client, 150, this.height - 100,
                60, this.height - 40,
                25
        );
        this.addDrawableChild(playerList);

        // Challenge List (Center)
        challengeList = new ChallengeListWidget(
                this.client, 200, this.height - 150,
                centerX - 100, 80,
                30
        );
        this.addDrawableChild(challengeList);

        // Digital Timer (Top right)
        timerWidget = new DigitalTimerWidget(
                this.width - 180, 60, 160, 80,
                this::onTimerChanged
        );
        this.addDrawableChild(timerWidget);

        // Ready Button (Bottom center)
        readyButton = ButtonWidget.builder(
                Text.literal("Ready").formatted(Formatting.GREEN),
                this::toggleReady
        ).dimensions(centerX - 100, this.height - 50, 90, 20).build();
        this.addDrawableChild(readyButton);

        // Force Start Button (Admin only, next to ready)
        forceStartButton = ButtonWidget.builder(
                Text.literal("Force Start").formatted(Formatting.RED),
                this::forceStart
        ).dimensions(centerX + 10, this.height - 50, 90, 20).build();
        forceStartButton.active = isAdmin;
        this.addDrawableChild(forceStartButton);

        // Right side buttons
        int rightX = this.width - 110;
        int buttonY = 160;

        // Challenge Menu Button
        challengeMenuButton = ButtonWidget.builder(
                Text.literal("⚔ Challenges"),
                this::openChallengeMenu
        ).dimensions(rightX, buttonY, 100, 20).build();
        challengeMenuButton.active = isAdmin;
        this.addDrawableChild(challengeMenuButton);

        // Settings Button
        settingsButton = ButtonWidget.builder(
                Text.literal("⚙ Settings"),
                this::openSettings
        ).dimensions(rightX, buttonY + 25, 100, 20).build();
        settingsButton.active = isAdmin;
        this.addDrawableChild(settingsButton);

        // Goal/Race Button
        goalRaceButton = ButtonWidget.builder(
                Text.literal("🏁 Goal/Race"),
                this::openGoalRace
        ).dimensions(rightX, buttonY + 50, 100, 20).build();
        goalRaceButton.active = isAdmin;
        this.addDrawableChild(goalRaceButton);

        // Request initial data from server
        requestServerData();
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark background
        this.renderBackground(context, mouseX, mouseY, delta);

        // Draw section labels
        context.drawText(this.textRenderer,
                Text.literal("Players").formatted(Formatting.WHITE, Formatting.UNDERLINE),
                20, 45, 0xFFFFFF, false);

        context.drawText(this.textRenderer,
                Text.literal("Active Challenges").formatted(Formatting.WHITE, Formatting.UNDERLINE),
                this.width / 2 - 100, 65, 0xFFFFFF, false);

        context.drawText(this.textRenderer,
                Text.literal("Timer Config").formatted(Formatting.WHITE, Formatting.UNDERLINE),
                this.width - 180, 45, 0xFFFFFF, false);

        super.render(context, mouseX, mouseY, delta);
    }

    private void toggleReady(ButtonWidget button) {
        isReady = !isReady;
        button.setMessage(Text.literal(isReady ? "Unready" : "Ready")
                .formatted(isReady ? Formatting.RED : Formatting.GREEN));

        // Send to server
        if (this.client != null && this.client.player != null) {
            // Network call to update ready status
        }
    }

    private void forceStart(ButtonWidget button) {
        if (isAdmin) {
            // Send force start to server
        }
    }

    private void openChallengeMenu(ButtonWidget button) {
        if (this.client != null) {
            this.client.setScreen(new ChallengeSelectScreen(this));
        }
    }

    private void openSettings(ButtonWidget button) {
        if (this.client != null) {
            this.client.setScreen(new SettingsScreen(this));
        }
    }

    private void openGoalRace(ButtonWidget button) {
        if (this.client != null) {
            this.client.setScreen(new GoalRaceScreen(this));
        }
    }

    private void onTimerChanged(int days, int hours, int minutes, int seconds) {
        // Send timer update to server
        if (this.client != null && this.client.player != null) {
            // Network call: /timer set d,hh:mm:ss
        }
    }

    public void updateTimer(String time, String color, String state) {
        if (timerWidget != null) {
            timerWidget.updateTime(time, color, state);
        }
    }

    public void updateChallengeState(String challengeId, boolean active) {
        if (challengeList != null) {
            challengeList.updateChallenge(challengeId, active);
        }
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
        if (forceStartButton != null) forceStartButton.active = admin;
        if (challengeMenuButton != null) challengeMenuButton.active = admin;
        if (settingsButton != null) settingsButton.active = admin;
        if (goalRaceButton != null) goalRaceButton.active = admin;
    }

    private void requestServerData() {
        // Request current state from server
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
