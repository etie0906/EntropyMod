package org.EntropyMod.entropymod.client.menu.widgets;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class DigitalTimerWidget extends ClickableWidget {
    private int days = 0;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private String state = "STOPPED";
    private String color = "white";
    private final Consumer<int[]> onChange;

    private final ButtonWidget[] plusButtons = new ButtonWidget[4];
    private final ButtonWidget[] minusButtons = new ButtonWidget[4];

    public DigitalTimerWidget(int x, int y, int width, int height, Consumer<int[]> onChange) {
        super(x, y, width, height, Text.empty());
        this.onChange = onChange;
        initButtons();
    }

    private void initButtons() {
        int segmentWidth = this.getWidth() / 4;

        for (int i = 0; i < 4; i++) {
            final int index = i;
            plusButtons[i] = ButtonWidget.builder(
                    Text.literal("+").formatted(Formatting.GREEN),
                    b -> increment(index)
            ).dimensions(this.getX() + i * segmentWidth + 10, this.getY(), 20, 12).build();
        }

        for (int i = 0; i < 4; i++) {
            final int index = i;
            minusButtons[i] = ButtonWidget.builder(
                    Text.literal("-").formatted(Formatting.RED),
                    b -> decrement(index)
            ).dimensions(this.getX() + i * segmentWidth + 10, this.getY() + 50, 20, 12).build();
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(this.getX(), this.getY() + 15, this.getX() + this.getWidth(), this.getY() + 45, 0x88000000);

        int segmentWidth = this.getWidth() / 4;
        String[] labels = {"Days", "Hours", "Mins", "Secs"};
        int[] values = {days, hours, minutes, seconds};

        for (int i = 0; i < 4; i++) {
            int segX = this.getX() + i * segmentWidth;

            context.drawCenteredTextWithShadow(
                    net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    labels[i], segX + segmentWidth / 2, this.getY() + 18, 0xAAAAAA
            );

            String valStr = String.format("%02d", values[i]);
            if (i == 0) valStr = String.valueOf(values[i]);

            int colorCode = getColorCode();
            context.drawCenteredTextWithShadow(
                    net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    Text.literal(valStr).styled(s -> s.withColor(colorCode)),
                    segX + segmentWidth / 2, this.getY() + 30, colorCode
            );
        }

        for (ButtonWidget btn : plusButtons) btn.render(context, mouseX, mouseY, delta);
        for (ButtonWidget btn : minusButtons) btn.render(context, mouseX, mouseY, delta);
    }

    private int getColorCode() {
        return switch (state) {
            case "RUNNING" -> 0x00FF00;
            case "PAUSED"  -> 0xFFFF00;
            case "STOPPED" -> 0x888888;
            default        -> 0xFFFFFF;
        };
    }

    private void increment(int index) {
        switch (index) {
            case 0 -> days++;
            case 1 -> hours   = (hours   + 1) % 24;
            case 2 -> minutes = (minutes + 1) % 60;
            case 3 -> seconds = (seconds + 1) % 60;
        }
        notifyChange();
    }

    private void decrement(int index) {
        switch (index) {
            case 0 -> days    = Math.max(0, days - 1);
            case 1 -> hours   = (hours   - 1 + 24) % 24;
            case 2 -> minutes = (minutes - 1 + 60) % 60;
            case 3 -> seconds = (seconds - 1 + 60) % 60;
        }
        notifyChange();
    }

    private void notifyChange() {
        if (onChange != null) {
            onChange.accept(new int[]{days, hours, minutes, seconds});
        }
    }

    public void updateTime(String timeStr, String color, String state) {
        this.color = color;
        this.state = state;

        try {
            if (timeStr.contains("Day")) {
                String[] parts = timeStr.split(" ");
                days = Integer.parseInt(parts[0]);
                String[] timeParts = parts[parts.length - 1].split(":");
                hours   = Integer.parseInt(timeParts[0]);
                minutes = Integer.parseInt(timeParts[1]);
                seconds = Integer.parseInt(timeParts[2]);
            } else {
                String[] parts = timeStr.split(":");
                hours   = Integer.parseInt(parts[0]);
                minutes = Integer.parseInt(parts[1]);
                seconds = Integer.parseInt(parts[2]);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mouseX = click.x();
        double mouseY = click.y();
        for (ButtonWidget btn : plusButtons) {
            if (btn.isMouseOver(mouseX, mouseY)) {
                return btn.mouseClicked(click, bl);
            }
        }
        for (ButtonWidget btn : minusButtons) {
            if (btn.isMouseOver(mouseX, mouseY)) {
                return btn.mouseClicked(click, bl);
            }
        }
        return super.mouseClicked(click, bl);
    }

    @Override
    protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
    }
}