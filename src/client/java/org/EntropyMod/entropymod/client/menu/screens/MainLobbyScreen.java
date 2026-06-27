package org.EntropyMod.entropymod.client.menu.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.EntropyMod.entropymod.client.menu.widgets.DigitalTimerWidget;

import java.util.HashSet;
import java.util.Set;

public class MainLobbyScreen extends Screen {
    private static final int SIDEBAR_WIDTH = 130;
    private static final int SIDEBAR_PAD = 8;

    private final Screen parent;
    private int selectedCategory = 0;
    private int contentX, contentY, contentW, contentH;
    private String versionString = "";

    private TextWidget timerDisplay;
    private FlatButton startBtn, pauseBtn, resumeBtn, stopBtn;
    private FlatButton colorBtn;
    private final Set<String> activeChallenges = new HashSet<>();
    private final Set<String> selectedChallengeIds = new HashSet<>();

    public MainLobbyScreen() {
        this(null);
    }

    public MainLobbyScreen(Screen parent) {
        super(Text.literal("EntropyMod Challenges"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        contentX = SIDEBAR_WIDTH + 15;
        contentY = 50;
        contentW = width - contentX - 15;
        contentH = height - contentY - 40;

        net.fabricmc.loader.api.ModContainer container =
                net.fabricmc.loader.api.FabricLoader.getInstance()
                        .getModContainer("entropymod").orElse(null);
        versionString = container != null
                ? container.getMetadata().getVersion().getFriendlyString()
                : "unknown";

        buildContent();
    }

    private void buildContent() {
        clearChildren();
        switch (selectedCategory) {
            case 0 -> buildTimerPanel();
            case 1 -> buildChallengesPanel();
            case 2 -> buildSettingsPanel();
        }
    }

    private void buildTimerPanel() {
        int cx = contentX;
        int cy = contentY;
        int cw = contentW;

        addDrawableChild(new TextWidget(cx, cy, cw, 20,
                Text.literal("Timer Controls").formatted(Formatting.BOLD, Formatting.GOLD),
                textRenderer));

        addDrawableChild(new TextWidget(cx, cy + 24, cw, 20,
                Text.literal("Configure your timer and control the challenge clock.").formatted(Formatting.DARK_GRAY),
                textRenderer));

        timerDisplay = new TextWidget(cx, cy + 50, cw, 30,
                Text.literal("00:00").formatted(Formatting.WHITE), textRenderer);
        addDrawableChild(timerDisplay);

        addDrawableChild(new DigitalTimerWidget(cx, cy + 90,
                Math.min(cw, 300), 70,
                values -> {
                    int total = values[0] * 86400 + values[1] * 3600 + values[2] * 60 + values[3];
                    sendCmd("timer set " + total);
                }));

        int by = cy + 180;
        startBtn = new FlatButton(cx, by, 80, 22, "Start", 0xFF2E7D32, 0xFF4CAF50,
                () -> sendCmd("timer resume"));
        addDrawableChild(startBtn);

        pauseBtn = new FlatButton(cx + 88, by, 80, 22, "Pause", 0xFF8D6E00, 0xFFFFB300,
                () -> sendCmd("timer pause"));
        addDrawableChild(pauseBtn);

        resumeBtn = new FlatButton(cx + 176, by, 80, 22, "Resume", 0xFF00695C, 0xFF26A69A,
                () -> sendCmd("timer resume"));
        addDrawableChild(resumeBtn);

        stopBtn = new FlatButton(cx + 264, by, 80, 22, "Stop", 0xFFB71C1C, 0xFFEF5350,
                () -> sendCmd("timer stop"));
        addDrawableChild(stopBtn);

        colorBtn = new FlatButton(cx, by + 32, 120, 22, "Timer Color", 0xFF37474F, 0xFF546E7A,
                () -> {
                    if (client != null) client.setScreen(new TimerColorConfigScreen(this));
                });
        addDrawableChild(colorBtn);
    }

    private void buildChallengesPanel() {
        int cx = contentX;
        int cy = contentY;
        int cw = contentW;

        addDrawableChild(new TextWidget(cx, cy, cw, 20,
                Text.literal("Available Challenges").formatted(Formatting.BOLD, Formatting.GOLD),
                textRenderer));

        addDrawableChild(new TextWidget(cx, cy + 24, cw, 20,
                Text.literal("Click to toggle challenges, then press Start Selected to begin.")
                        .formatted(Formatting.DARK_GRAY),
                textRenderer));

        int bx = cx;
        int by = cy + 50;
        int bw = 200;
        int bh = 28;

        addChallengeToggle(bx, by, bw, bh, "dummy", "Dummy / None");
        addChallengeToggle(bx, by + bh + 6, bw, bh, "movement_speed", "Movement Speed");
        addChallengeToggle(bx, by + 2 * (bh + 6), bw, bh, "random_item", "Random Item");

        int sx = bx + bw + 20;
        addDrawableChild(new FlatButton(sx, by, 120, bh, "▶ Start Selected", 0xFF2E7D32, 0xFF4CAF50,
                () -> {
                    if (MinecraftClient.getInstance().player != null) {
                        for (String id : selectedChallengeIds) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("challenge start " + id);
                        }
                    }
                    selectedChallengeIds.clear();
                    buildContent();
                }));

        addDrawableChild(new FlatButton(sx, by + bh + 6, 120, bh, "■ Stop All", 0xFF6D1C1C, 0xFFD32F2F,
                () -> {
                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand("challenge stop");
                    }
                }));

        addDrawableChild(new FlatButton(sx, by + 2 * (bh + 6), 120, bh, "Test", 0xFF37474F, 0xFF546E7A,
                () -> {
                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand("challenge test");
                    }
                }));
    }

    private void addChallengeToggle(int x, int y, int w, int h, String id, String label) {
        addDrawableChild(new FlatButton(x, y, w, h, label, 0xFF37474F, 0xFF546E7A,
                () -> {
                    if (selectedChallengeIds.contains(id))
                        selectedChallengeIds.remove(id);
                    else
                        selectedChallengeIds.add(id);
                    rebuildChallengesPanel();
                }) {
            @Override
            public void renderWidget(DrawContext context, int mx, int my, float delta) {
                boolean isSelected = selectedChallengeIds.contains(id);
                boolean isActive = activeChallenges.contains(id);
                boolean hovered = isMouseOver(mx, my);

                int bg = isActive ? 0xFF1B5E20 : (isSelected ? 0xFF1A237E : (hovered ? 0xFF2E3A54 : 0xFF1E2838));
                int border = isActive ? 0xFF4CAF50 : (isSelected ? 0xFF448AFF : (hovered ? 0xFF546E7A : 0x00000000));
                context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bg);
                context.fill(getX(), getY(), getX() + getWidth(), getY() + 2, border);

                int txtColor = isActive ? 0xFF81C784 : (isSelected ? 0xFF82B1FF : (hovered ? 0xFFFFFFFF : 0xFFCCCCCC));
                String prefix = isActive ? "●" : (isSelected ? "[X]" : "[ ]");
                String display = prefix + " " + label;
                TextRenderer tr = MinecraftClient.getInstance().textRenderer;
                context.drawText(tr, Text.literal(display),
                        getX() + 8, getY() + (getHeight() - 8) / 2, txtColor, false);
            }
        });
    }

    private void rebuildChallengesPanel() {
        if (selectedCategory == 1) buildContent();
    }

    private void buildSettingsPanel() {
        int cx = contentX;
        int cy = contentY;

        addDrawableChild(new TextWidget(cx, cy, contentW, 20,
                Text.literal("Settings").formatted(Formatting.BOLD, Formatting.GOLD),
                textRenderer));

        addDrawableChild(new FlatButton(cx, cy + 30, 130, 22, "Timer Color", 0xFF37474F, 0xFF546E7A,
                () -> {
                    if (client != null) client.setScreen(new TimerColorConfigScreen(this));
                }));

        addDrawableChild(new FlatButton(cx, cy + 60, 130, 22, "Reset Timer", 0xFF6D1C1C, 0xFFD32F2F,
                () -> sendCmd("timer set 0")));
    }

    private void sendCmd(String cmd) {
        if (MinecraftClient.getInstance().player != null)
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(cmd);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xFF0A0A1A);
        super.render(context, mouseX, mouseY, delta);
        drawSidebar(context, mouseX, mouseY);

        String versionLabel = "Version: " + versionString;
        int vw = textRenderer.getWidth(versionLabel);
        context.drawText(textRenderer, versionLabel, width - vw - 10, height - 15, 0xFF555555, false);
    }

    private void drawSidebar(DrawContext context, int mouseX, int mouseY) {
        context.fill(0, 0, SIDEBAR_WIDTH, height, 0xFF151528);
        context.fill(SIDEBAR_WIDTH, 0, SIDEBAR_WIDTH + 2, height, 0xFF333355);

        context.drawText(textRenderer,
                Text.literal("EntropyMod").formatted(Formatting.BOLD, Formatting.GOLD),
                SIDEBAR_PAD, 15, 0xFFFFFFFF, false);

        String[] tabNames = {"Timer", "Challenges", "Settings"};
        int startY = 50;
        int btnH = 35;
        int spacing = 4;

        for (int i = 0; i < tabNames.length; i++) {
            int btnY = startY + i * (btnH + spacing);
            boolean hovered = mouseX >= SIDEBAR_PAD && mouseX <= SIDEBAR_WIDTH - SIDEBAR_PAD
                    && mouseY >= btnY && mouseY <= btnY + btnH;
            boolean selected = i == selectedCategory;

            int boxColor = selected ? 0xFF3A4C6F : (hovered ? 0xFF2E3A54 : 0xFF1E2838);
            context.fill(SIDEBAR_PAD, btnY, SIDEBAR_WIDTH - SIDEBAR_PAD, btnY + btnH, boxColor);

            if (selected)
                context.fill(SIDEBAR_PAD, btnY, SIDEBAR_PAD + 2, btnY + btnH, 0xFF6688CC);
        }

        // Sidebar-Text: UNBEDINGT in eigenem Loop am Ende, immer, ohne Bedingung
        for (int i = 0; i < tabNames.length; i++) {
            int btnY = startY + i * (btnH + spacing);
            boolean selected = i == selectedCategory;
            int textColor = selected ? 0xFFFFFFFF : 0xFFAAAAAA;
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal(tabNames[i]),
                    SIDEBAR_WIDTH / 2, btnY + (btnH - 8) / 2, textColor);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mx = click.x();
        double my = click.y();
        String[] tabNames = {"Timer", "Challenges", "Settings"};
        int startY = 50;
        int btnH = 35;
        int spacing = 4;
        for (int i = 0; i < tabNames.length; i++) {
            int btnY = startY + i * (btnH + spacing);
            if (mx >= SIDEBAR_PAD && mx <= SIDEBAR_WIDTH - SIDEBAR_PAD
                    && my >= btnY && my <= btnY + btnH) {
                if (selectedCategory != i) {
                    selectedCategory = i;
                    buildContent();
                }
                return true;
            }
        }
        return super.mouseClicked(click, bl);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void updateTimer(String time, String color, String state) {
        if (timerDisplay != null) {
            Text styled;
            try {
                int hex = Integer.parseInt(color.replace("#", ""), 16);
                styled = Text.literal(time).styled(s -> s.withColor(hex));
            } catch (Exception e) {
                styled = Text.literal(time).formatted(Formatting.WHITE);
            }
            timerDisplay.setMessage(styled);
        }
    }

    public void updateChallengeState(String challengeId, boolean active) {
        if (active) activeChallenges.add(challengeId);
        else activeChallenges.remove(challengeId);
    }

    public static class FlatButton extends ClickableWidget {
        protected final int bgColor;
        protected final int hoverColor;
        private final String label;
        private final Runnable onClick;

        public FlatButton(int x, int y, int w, int h, String label, int bgColor, int hoverColor, Runnable onClick) {
            super(x, y, w, h, Text.literal(label));
            this.label = label;
            this.bgColor = bgColor;
            this.hoverColor = hoverColor;
            this.onClick = onClick;
        }

        public FlatButton(int x, int y, int w, int h, String label, int accentColor, Runnable onClick) {
            this(x, y, w, h, label, 0xFF37474F, accentColor, onClick);
        }

        @Override
        public void renderWidget(DrawContext context, int mx, int my, float delta) {
            boolean hovered = isMouseOver(mx, my);
            int color = hovered ? hoverColor : bgColor;
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;
            context.drawText(tr, Text.literal(label),
                    getX() + (getWidth() - tr.getWidth(label)) / 2,
                    getY() + (getHeight() - 8) / 2,
                    0xFFFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(Click click, boolean bl) {
            if (isMouseOver(click.x(), click.y())) {
                onClick.run();
                return true;
            }
            return false;
        }

        @Override
        protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
        }
    }
}
