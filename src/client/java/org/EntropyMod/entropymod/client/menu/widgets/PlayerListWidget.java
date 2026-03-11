package org.EntropyMod.entropymod.client.menu.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class PlayerListWidget extends AlwaysSelectedEntryListWidget<PlayerListWidget.PlayerEntry> {
    private final MinecraftClient client;

    // FIX: AlwaysSelectedEntryListWidget constructor no longer takes a separate 'bottom' int.
    // Old: (client, width, height, top, bottom, itemHeight)
    // New: (client, width, height, top, itemHeight)
    public PlayerListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.client = client;
        refreshPlayers();
    }

    public void refreshPlayers() {
        this.clearEntries();

        if (client.getNetworkHandler() == null) return;

        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            boolean isAdmin = false;
            boolean isReady = false;
            int ping = entry.getLatency();

            // FIX: GameProfile.getId() / getName() are now record accessors: .id() and .name()
            this.addEntry(new PlayerEntry(
                    entry.getProfile().getId(),
                    entry.getProfile().getName(),
                    ping,
                    isReady,
                    isAdmin
            ));
        }
    }

    public class PlayerEntry extends AlwaysSelectedEntryListWidget.Entry<PlayerEntry> {
        private final UUID uuid;
        private final String name;
        private final int ping;
        private final boolean ready;
        private final boolean isAdmin;

        public PlayerEntry(UUID uuid, String name, int ping, boolean ready, boolean isAdmin) {
            this.uuid = uuid;
            this.name = name;
            this.ping = ping;
            this.ready = ready;
            this.isAdmin = isAdmin;
        }

        // FIX: render signature changed — 'boolean hovered' moved to after mouseY, 'float tickDelta' last.
        // Old: render(DrawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
        // New: render(DrawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
        // The signature itself is the same shape — the issue was a missing @Override match.
        // In 1.21.11 the abstract method is:
        //   render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {

            context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);

            int textX = x + 30;

            if (isAdmin) {
                context.drawText(client.textRenderer,
                        Text.literal("👑").formatted(Formatting.GOLD),
                        textX, y + 5, 0xFFFFFF, false);
                textX += 15;
            }

            String readyIcon = ready ? "✔" : "✘";
            Formatting readyColor = ready ? Formatting.GREEN : Formatting.RED;
            context.drawText(client.textRenderer,
                    Text.literal(readyIcon).formatted(readyColor),
                    textX, y + 5, 0xFFFFFF, false);
            textX += 15;

            context.drawText(client.textRenderer, name, textX, y + 5, 0xFFFFFF, false);

            String pingText = ping + "ms";
            int pingColor = getPingColor(ping);
            context.drawText(client.textRenderer, pingText,
                    x + entryWidth - 50, y + 5, pingColor, false);
        }

        private int getPingColor(int ping) {
            if (ping < 100) return 0x00FF00;
            if (ping < 200) return 0xFFFF00;
            return 0xFF0000;
        }

        @Override
        public Text getNarration() {
            return Text.literal(name);
        }
    }
}