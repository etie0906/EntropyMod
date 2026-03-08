package org.EntropyMod.entropymod.client.menu.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListWidget extends AlwaysSelectedEntryListWidget<PlayerListWidget.PlayerEntry> {
    private final MinecraftClient client;

    public PlayerListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.client = client;
        refreshPlayers();
    }

    public void refreshPlayers() {
        this.clearEntries();

        if (client.getNetworkHandler() == null) return;

        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            boolean isAdmin = false; // Get from server
            boolean isReady = false; // Get from server
            int ping = entry.getLatency();

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

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {

            // Background
            context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);

            // Player head
            // Draw player head texture here

            int textX = x + 30;

            // Admin crown
            if (isAdmin) {
                context.drawText(client.textRenderer,
                        Text.literal("👑").formatted(Formatting.GOLD),
                        textX, y + 5, 0xFFFFFF, false);
                textX += 15;
            }

            // Ready indicator
            String readyIcon = ready ? "✔" : "✘";
            Formatting readyColor = ready ? Formatting.GREEN : Formatting.RED;
            context.drawText(client.textRenderer,
                    Text.literal(readyIcon).formatted(readyColor),
                    textX, y + 5, 0xFFFFFF, false);
            textX += 15;

            // Player name
            context.drawText(client.textRenderer, name, textX, y + 5, 0xFFFFFF, false);

            // Ping
            String pingText = ping + "ms";
            int pingColor = getPingColor(ping);
            context.drawText(client.textRenderer, pingText,
                    x + entryWidth - 50, y + 5, pingColor, false);
        }

        private int getPingColor(int ping) {
            if (ping < 100) return 0x00FF00; // Green
            if (ping < 200) return 0xFFFF00; // Yellow
            return 0xFF0000; // Red
        }

        @Override
        public Text getNarration() {
            return Text.literal(name);
        }
    }
}
