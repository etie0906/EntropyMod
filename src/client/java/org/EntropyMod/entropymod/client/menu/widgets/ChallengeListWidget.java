package org.EntropyMod.entropymod.client.menu.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class ChallengeListWidget extends AlwaysSelectedEntryListWidget<ChallengeListWidget.ChallengeEntry> {
    private final MinecraftClient client;
    private final Map<String, Boolean> challengeStates = new HashMap<>();

    // FIX: AlwaysSelectedEntryListWidget constructor no longer takes a separate 'bottom' int.
    // Old: (client, width, height, top, bottom, itemHeight)  ← 6 args
    // New: (client, width, height, top, itemHeight)          ← 5 args
    public ChallengeListWidget(MinecraftClient client, int width, int height, int left, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.client = client;
        this.setX(left);

        addChallenge("dummy", "Dummy Challenge", "Test challenge", false);
    }

    public void addChallenge(String id, String name, String description, boolean active) {
        challengeStates.put(id, active);
        this.addEntry(new ChallengeEntry(id, name, description, active));
    }

    public void updateChallenge(String id, boolean active) {
        challengeStates.put(id, active);
        // Refresh entry — full refresh for simplicity
    }

    public class ChallengeEntry extends AlwaysSelectedEntryListWidget.Entry<ChallengeEntry> {
        private final String id;
        private final String name;
        private final String description;
        private boolean active;

        public ChallengeEntry(String id, String name, String description, boolean active) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.active = active;
        }

        // FIX: EntryListWidget.Entry.render abstract method in 1.21.11 is:
        //   render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks)
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int x = getX();
            int y = getY();
            int entryWidth = getWidth();
            int entryHeight = getHeight();

            int bgColor = active ? 0x4400FF00 : 0x33FFFFFF;
            if (hovered) bgColor = 0x55FFFFFF;

            context.fill(x, y, x + entryWidth, y + entryHeight, bgColor);

            String status = active ? "●" : "○";
            int statusColor = active ? 0x00FF00 : 0x888888;
            context.drawText(client.textRenderer, status, x + 5, y + 8, statusColor, false);

            context.drawText(client.textRenderer,
                    Text.literal(name).formatted(active ? Formatting.GREEN : Formatting.GRAY),
                    x + 20, y + 5, 0xFFFFFF, false);

            String desc = description.length() > 30 ? description.substring(0, 27) + "..." : description;
            context.drawText(client.textRenderer,
                    Text.literal(desc).formatted(Formatting.DARK_GRAY),
                    x + 20, y + 17, 0x888888, false);
        }

        @Override
        public Text getNarration() {
            return Text.literal(name);
        }
    }
}