package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.AbstractInventoryEvent;
import com.a3ot.disastermod.events.subclasses.AbstractRandomSlotInventoryEvent;
import com.a3ot.disastermod.events.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HappyBirthdayEvent extends AbstractRandomSlotInventoryEvent implements AbstractInventoryEvent.IBasicEvent {
    private static ServerPlayer birthdayBoy;
    private NonNullList<ItemStack> chestItems;

    @Override
    protected int getSlotsToProcessCount() {
        return 3;
    }

    @Override
    public void onStart(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) return;
        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers().stream().filter(Utils::isPlayerValid).toList();
        if (players.isEmpty()) return;
        birthdayBoy = players.get(level.random.nextInt(players.size()));
        BlockPos pos = birthdayBoy.blockPosition();
        int x = pos.getX();
        int z = pos.getZ();
        int y = pos.getY();

        ServerLevel birthdayLevel = (ServerLevel) birthdayBoy.level();

        Utils.airFilling(
                birthdayLevel,
                new BlockPos(x, y, z),
                new BlockPos(x + 1, y + 1, z + 1)
        );

        birthdayBoy.teleportTo(birthdayLevel, x + 0.5, y, z + 0.5, 0, 25);
        spawnFireworks(birthdayLevel, birthdayBoy);

        birthdayLevel.setBlock(pos.above().south(2),
                Blocks.CANDLE_CAKE.defaultBlockState().setValue(CandleCakeBlock.LIT, true), 3);
        birthdayLevel.setBlock(pos.south(2), Blocks.CHEST.defaultBlockState(), 3);
        birthdayLevel.getServer().submit(() -> {
            BlockEntity blockEntity = birthdayLevel.getBlockEntity(pos.south(2));
            if (!(blockEntity instanceof ChestBlockEntity chest)) return;

            ResourceKey<LootTable> endCityLoot = ResourceKey.create(
                    Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure")
            );
            chest.setLootTable(endCityLoot, birthdayLevel.random.nextLong());


            level.players().stream().filter(player -> player != birthdayBoy && Utils.isPlayerValid(player)).forEach(player -> {
                chestItems = chest.getItems();
                processRandomSlots(level, player);
                player.getInventory().setChanged();
                chest.setChanged();
            });

            NonNullList<ItemStack> chestItems = chest.getItems();
            Collections.shuffle(chestItems);
            chest.setItems(chestItems);
            chest.setChanged();
        });
    }



    @Override
    public ItemStack modifyItem(ItemStack stack) {
        boolean added = false;
        for (int i = 0; i < chestItems.size(); i++) {
            if (chestItems.get(i).isEmpty()) {
                chestItems.set(i, stack.copy());
                added = true;
                break;
            }
        }
        if (!added) return stack;
        return ItemStack.EMPTY;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.LIGHT_PURPLE;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }

    @Override
    public Component getMessage(AbstractEvent event) {
        if (birthdayBoy != null) return
                Component.literal("[Disaster Mod] ")
                        .append(Component.translatable(event.getName())
                        .append(Component.translatable("disastermod.configuration.happyBirthday.playerNickName", birthdayBoy.getName()))
                        .withStyle(event.getColor()))
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Component.translatable(event.getDescription()))));

        return super.getMessage(event);
    }

    private void spawnFireworks(ServerLevel level, ServerPlayer player) {
        int[][] colors = {{255, 0, 0}, {0, 255, 0}, {0, 0, 255}};
        Vec3 playerPos = player.position();

        for (int i = 0; i < 3; i++) {
            double angle = Math.toRadians(i * 120);
            double radius = 3.0;
            double x = playerPos.x() + radius * Math.cos(angle);
            double z = playerPos.z() + radius * Math.sin(angle);
            double y = playerPos.y();

            ItemStack fireworkStack = createFireworkWithColor(colors[i][0], colors[i][1], colors[i][2]);

            FireworkRocketEntity firework = new FireworkRocketEntity(level, fireworkStack, x, y, z, false);

            level.addFreshEntity(firework);
        }
    }

    private ItemStack createFireworkWithColor(int red, int green, int blue) {
        IntList colorList = new IntArrayList();
        int combinedColor = (red << 16) | (green << 8) | blue;
        colorList.add(combinedColor);

        FireworkExplosion explosion = new FireworkExplosion(
                FireworkExplosion.Shape.STAR,
                colorList,
                IntList.of(),
                true,
                true
        );

        Fireworks fireworks = new Fireworks(1, List.of(explosion));

        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        fireworkStack.set(DataComponents.FIREWORKS, fireworks);

        return fireworkStack;
    }
}
