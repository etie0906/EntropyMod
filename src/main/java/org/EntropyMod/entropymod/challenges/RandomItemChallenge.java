package org.EntropyMod.entropymod.challenges;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class RandomItemChallenge implements Challenge {
    private static boolean active = false;
    private static final Random random = new Random();
    private static final Map<Block, Item> blockDropMap = new HashMap<>();
    private static final Map<Item, Item> itemReplacementMap = new HashMap<>();

    private int ticksUntilNextItem = 0;
    private int ticksUntilRandomize = 0;
    private int stackSize = 1;
    private int finalStackSize = 1;
    private MinecraftServer server;

    public static Item getBlockDrop(Block block) {
        return blockDropMap.get(block);
    }

    public static Item getItemReplacement(Item item) {
        return itemReplacementMap.get(item);
    }

    @Override
    public String getId() { return "random_item"; }

    @Override
    public String getName() { return "Random Item"; }

    @Override
    public Text getDescription() {
        return Text.literal("Randomizes drops and gives random items to players");
    }

    @Override
    public void start(MinecraftServer server, List<ServerPlayerEntity> players) {
        this.server = server;
        active = true;
        ticksUntilNextItem = 1200;
        ticksUntilRandomize = 600;
        initializeDropMaps();

        for (ServerPlayerEntity player : players) {
            player.sendMessage(Text.literal(String.format("§aRandom Item Challenge gestartet! §e(Stack-Größe: %d)", finalStackSize)), false);
            player.sendMessage(Text.literal("§eBlock- und Mob-Drops wurden randomisiert!"), false);
        }
    }

    @Override
    public void stop(MinecraftServer server) {
        active = false;
        blockDropMap.clear();
        itemReplacementMap.clear();
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.literal("§cRandom Item Challenge gestoppt."), false);
            }
        }
    }

    @Override
    public void pause(MinecraftServer server) {
        active = false;
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.literal("§eRandom Item Challenge pausiert."), false);
            }
        }
    }

    @Override
    public void resume(MinecraftServer server) {
        active = true;
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.literal("§aRandom Item Challenge fortgesetzt."), false);
            }
        }
    }

    @Override
    public void tick(MinecraftServer server) {
        if (!active || server == null) return;

        if (finalStackSize == 0) {
            ticksUntilRandomize--;
            if (ticksUntilRandomize <= 0) {
                finalStackSize = 1 + random.nextInt(64);
                ticksUntilRandomize = 600;
                String msg = "§eZufällige Stack-Größe: §6" + finalStackSize;
                for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                    p.sendMessage(Text.literal(msg), false);
                }
            }
        }

        ticksUntilNextItem--;
        if (ticksUntilNextItem <= 0) {
            giveRandomItemToPlayers();
            ticksUntilNextItem = 1200;
        }
    }

    @Override
    public boolean isActive() { return active; }

    @Override
    public void setActive(boolean active) { RandomItemChallenge.active = active; }

    public void skipWait() {
        ticksUntilNextItem = 0;
    }

    public void setStackSize(int size) {
        this.stackSize = size;
        this.finalStackSize = size;
        if (size == 0) {
            ticksUntilRandomize = 600;
        }
    }

    private void initializeDropMaps() {
        List<Item> allItems = Registries.ITEM.stream().toList();
        if (allItems.isEmpty()) return;

        for (Block block : Registries.BLOCK) {
            blockDropMap.put(block, allItems.get(random.nextInt(allItems.size())));
        }

        for (Item item : allItems) {
            itemReplacementMap.put(item, allItems.get(random.nextInt(allItems.size())));
        }
    }

    private void giveRandomItemToPlayers() {
        if (server == null) return;

        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        List<Item> allItems = Registries.ITEM.stream().toList();
        if (allItems.isEmpty()) return;

        Item randomItem = allItems.get(random.nextInt(allItems.size()));

        if (finalStackSize == 0) {
            finalStackSize = 1 + random.nextInt(64);
        }

        int effectiveSize = finalStackSize;

        for (ServerPlayerEntity player : players) {
            ItemStack stack = new ItemStack(randomItem, effectiveSize);
            boolean added = player.getInventory().insertStack(stack);
            if (!added) {
                player.dropItem(stack, false);
            }

            String itemName = randomItem.getName().getString();
            player.sendMessage(Text.literal("§bDu hast ein zufälliges Item erhalten: ")
                    .append(Text.literal(itemName))
                    .append(Text.literal(String.format(" §7(x%d)", effectiveSize))), false);
        }
    }

    public Map<UUID, Integer> getPlayerStackVotes() {
        return new HashMap<>();
    }

    public void addStackVote(UUID playerUuid, int vote) {
    }

    public void calculateFinalStackSize() {
        finalStackSize = stackSize;
    }
}
