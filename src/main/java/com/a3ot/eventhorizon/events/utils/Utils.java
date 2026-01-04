package com.a3ot.eventhorizon.events.utils;

import com.a3ot.eventhorizon.data.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static boolean isValidPlayer(Player player) {
        return player != null
                && player.isAlive()
                && !player.isCreative() && !player.isSpectator();
    }

    public static boolean isValidDeadPlayer(Player player) {
        return player != null
                && !player.isCreative() && !player.isSpectator();
    }

    public static void changeItemInHand(Player player, ItemStack newItem) {
        ItemStack oldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
        giveItem(player, oldItem);
    }

    public static void giveItem(Player player, ItemStack newItem) {
        if (newItem.isEmpty()) return;
        int count = newItem.getCount();
        int maxStackSize = newItem.getMaxStackSize();
        int maxTotalItems = maxStackSize * 100;
        if (count > maxTotalItems) {
            int itemsToProcess = count;
            while (itemsToProcess > 0) {
                int itemsForThisStack = Math.min(maxStackSize, itemsToProcess);
                itemsToProcess -= itemsForThisStack;
                ItemStack stackToGive = newItem.copyWithCount(itemsForThisStack);
                boolean addedToInventory = player.getInventory().add(stackToGive);
                if (addedToInventory && stackToGive.isEmpty()) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                            0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.containerMenu.broadcastChanges();
                } else {
                    if (!stackToGive.isEmpty()) {
                        ItemEntity entity = player.drop(stackToGive, false);
                        if (entity != null) {
                            entity.setNoPickUpDelay();
                            entity.setTarget(player.getUUID());
                        }
                    }
                }
            }
        } else {
            boolean addedToInventory = player.getInventory().add(newItem);
            if (addedToInventory && newItem.isEmpty()) {
                player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.MASTER, 1, 1);
            } else {
                if (!newItem.isEmpty()) {
                    ItemEntity entity = player.drop(newItem, false);
                    if (entity != null) {
                        entity.setNoPickUpDelay();
                        entity.setTarget(player.getUUID());
                    }
                }
            }
        }
    }


    public static void airFilling(ServerLevel level, int x1, int y1, int z1, int x2, int y2, int z2){
        airFilling(level, new BlockPos(x1, y1, z1), new BlockPos(x2, y2, z2));
    }

    public static void airFilling(ServerLevel level, BlockPos pos1, BlockPos pos2){
        BlockPos.betweenClosedStream(pos1, pos2).forEach(pos -> {
            BlockState state = level.getBlockState(pos);
            if (state.is(ModTags.Blocks.IMPORTANT_BLOCKS) || state.isEmpty()) return;
            if (state.is(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS)) level.destroyBlock(pos, true);
            else level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        });
    }

    public static void blockFilling(ServerLevel level, int x1, int y1, int z1, int x2, int y2, int z2, Block block){
        blockFilling(level, new BlockPos(x1, y1, z1), new BlockPos(x2, y2, z2), block);
    }

    public static void blockFilling(ServerLevel level, BlockPos pos1, BlockPos pos2, Block block){
        BlockPos.betweenClosedStream(pos1, pos2).forEach(pos -> {
            BlockState state = level.getBlockState(pos);
            if (state.is(ModTags.Blocks.IMPORTANT_BLOCKS)) return;
            if (state.is(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS)) level.destroyBlock(pos, true);
            else level.setBlock(pos, block.defaultBlockState(), 3);
        });
    }

    public static void blockFilling(ServerLevel level, BlockPos pos1, BlockPos pos2, BlockState blockState) {
        BlockPos.betweenClosedStream(pos1, pos2).forEach(pos -> {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.is(ModTags.Blocks.IMPORTANT_BLOCKS)) return;
            if (currentState.is(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS)) {
                level.destroyBlock(pos, true);
            } else {
                level.setBlock(pos, blockState, 3);
            }
        });
    }

    public static boolean isBlockPosHasTag(Level level, BlockPos pos, TagKey<Block> tag) {
        BlockState state = level.getBlockState(pos);
        return state.is(tag);
    }

    public static int lightManager(BlockPos pos, Level level){
        return lightManager(pos, level, 0, 0);
    }

    public static int lightManager(BlockPos pos, Level level, int skyLightModifier, int blockLightModifier){
        int skyLight;
        if (level.dimension() == Level.END) skyLight = 0;
        else if(level.dimension() == Level.NETHER) skyLight = 4;
        else{
            skyLight = level.getBrightness(LightLayer.SKY, pos);
            if (!level.isDay()) skyLight -= 11;
        }
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        return Math.max(skyLight + skyLightModifier, blockLight + blockLightModifier);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playItemActivation(ItemStack itemStack, int entityId) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level != null) {
            Entity entity = level.getEntity(entityId);
            if (entity instanceof Player player && player == mc.player) {
                mc.gameRenderer.displayItemActivation(itemStack);
            }
        }
    }

    private static final Map<LivingEntity, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_DURATION = 100;

    public static void ChorusTeleport(LivingEntity entity, Level level){
        ChorusTeleport(entity, level, 16, 48);
    }

    public static void ChorusTeleport(LivingEntity entity, Level level, int minDistance, int maxDistance) {
        if (minDistance > maxDistance) {
            int temp = minDistance;
            minDistance = maxDistance;
            maxDistance = temp;
        }

        long currentTime = entity.level().getGameTime();
        if (cooldowns.containsKey(entity) && cooldowns.get(entity) > currentTime) {
            return;
        }

        Vec3 originalPos = entity.position();
        int maxAttempts = 32;

        for (int i = 0; i < maxAttempts; i++) {
            double angle = entity.getRandom().nextDouble() * 2.0 * Math.PI;
            double distance = minDistance + entity.getRandom().nextDouble() * (maxDistance - minDistance);
            double dx = Math.cos(angle) * distance;
            double dz = Math.sin(angle) * distance;
            double newX = originalPos.x() + dx;
            double newZ = originalPos.z() + dz;
            double minY = level.getMinBuildHeight();
            double maxY = minY + level.getHeight() - 1;
            double newY = Mth.clamp(
                    originalPos.y() + (entity.getRandom().nextDouble() - 0.5) * (maxDistance - minDistance) * 0.5,
                    minY,
                    maxY
            );

            if (entity.isPassenger()) entity.stopRiding();

            Vec3 vec3 = entity.position();
            EntityTeleportEvent.ChorusFruit event = EventHooks.onChorusFruitTeleport(entity, newX, newY, newZ);
            if (event.isCanceled()) {
                continue;
            }
            if (entity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                cooldowns.put(entity, currentTime + COOLDOWN_DURATION);
                level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
                entity.resetFallDistance();
                SoundSource soundsource = SoundSource.PLAYERS;
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, soundsource);
                if (entity instanceof Player player) {
                    player.resetCurrentImpulseContext();
                }
                break;
            }
        }
    }

    public static boolean isOnCooldown(LivingEntity entity) {
        long currentTime = entity.level().getGameTime();
        return cooldowns.containsKey(entity) && cooldowns.get(entity) > currentTime;
    }

    public static void setCooldown(LivingEntity entity) {
        cooldowns.put(entity, entity.level().getGameTime() + COOLDOWN_DURATION);
    }

    public static void clearTeleportCooldowns() {
        cooldowns.clear();
    }
}
