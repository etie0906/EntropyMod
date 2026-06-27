package org.EntropyMod.entropymod.challenges;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class RandomItemChallenge implements Challenge {
    private boolean active = false;
    private int ticksUntilNextItem = 0;
    private final Random random = new Random();
    private int stackSize = 1;
    private int finalStackSize = 1;
    private MinecraftServer server;

    private final Map<Block, Item> blockDropMap = new HashMap<>();
    private final Map<Item, Item> itemReplacementMap = new HashMap<>();

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
        initializeDropMaps();
        registerEvents();

        for (ServerPlayerEntity player : players) {
            player.sendMessage(Text.literal(String.format("§aRandom Item Challenge gestartet! §e(Stack-Größe: %d)", stackSize)), false);
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

        ticksUntilNextItem--;
        if (ticksUntilNextItem <= 0) {
            giveRandomItemToPlayers();
            ticksUntilNextItem = 1200;
        }
    }

    @Override
    public boolean isActive() { return active; }

    @Override
    public void setActive(boolean active) { this.active = active; }

    public void skipWait() {
        ticksUntilNextItem = 0;
    }

    public void setStackSize(int size) {
        this.stackSize = size;
        this.finalStackSize = size;
    }

    private void initializeDropMaps() {
        List<Item> allItems = Registries.ITEM.stream().toList();
        if (allItems.isEmpty()) {
            System.err.println("Keine Items im Register gefunden.");
            return;
        }

        for (Block block : Registries.BLOCK) {
            Item randomItem = allItems.get(random.nextInt(allItems.size()));
            blockDropMap.put(block, randomItem);
        }

        for (Item item : allItems) {
            Item randomItem = allItems.get(random.nextInt(allItems.size()));
            itemReplacementMap.put(item, randomItem);
        }

        System.out.println("Block-Drops und Item-Ersetzungen wurden randomisiert.");
        System.out.println("Block-Mappings: " + blockDropMap.size() + ", Item-Mappings: " + itemReplacementMap.size());
    }

    private void registerEvents() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (!active) return true;
            if (!(player instanceof ServerPlayerEntity)) return true;

            Block block = state.getBlock();
            Item replacement = blockDropMap.get(block);
            if (replacement != null && !world.isClient()) {
                world.breakBlock(pos, false);
                world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);
                ItemStack drop = new ItemStack(replacement, 1);
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                return false;
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!active || server == null) return;
            if (entity.getEntityWorld().isClient()) return;

            server.execute(() -> {
                net.minecraft.world.World world = entity.getEntityWorld();
                if (world == null) return;

                Box box = entity.getBoundingBox().expand(5.0);
                List<ItemEntity> items = world.getEntitiesByClass(
                        ItemEntity.class, box, e -> true);

                for (ItemEntity itemEntity : items) {
                    ItemStack stack = itemEntity.getStack();
                    Item originalItem = stack.getItem();
                    Item replacement = itemReplacementMap.get(originalItem);

                    if (replacement != null) {
                        int count = stack.getCount();
                        ItemStack newStack = new ItemStack(replacement, count);
                        ItemEntity newEntity = new ItemEntity(
                                world,
                                itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                                newStack);
                        newEntity.setVelocity(itemEntity.getVelocity());
                        itemEntity.discard();
                        world.spawnEntity(newEntity);
                    }
                }
            });
        });
    }

    private void giveRandomItemToPlayers() {
        if (server == null) return;

        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        List<Item> allItems = Registries.ITEM.stream().toList();
        if (allItems.isEmpty()) {
            System.err.println("Keine Items im Register gefunden.");
            return;
        }

        Item randomItem = allItems.get(random.nextInt(allItems.size()));

        for (ServerPlayerEntity player : players) {
            ItemStack stack = new ItemStack(randomItem, finalStackSize);
            boolean added = player.getInventory().insertStack(stack);
            if (!added) {
                player.dropItem(stack, false);
            }

            String itemName = randomItem.getName().getString();
            player.sendMessage(Text.literal("§bDu hast ein zufälliges Item erhalten: ")
                    .append(Text.literal(itemName))
                    .append(Text.literal(String.format(" §7(x%d)", finalStackSize))), false);
        }
    }

    public Map<UUID, Integer> getPlayerStackVotes() {
        return new HashMap<>();
    }

    public void addStackVote(UUID playerUuid, int vote) {
        if (vote > 0) {
            // store vote
        }
    }

    public void calculateFinalStackSize() {
        finalStackSize = stackSize;
    }
}
