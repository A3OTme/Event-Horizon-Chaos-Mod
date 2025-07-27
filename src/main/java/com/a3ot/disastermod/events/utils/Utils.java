package com.a3ot.disastermod.events.utils;

import com.a3ot.disastermod.data.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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

    public static void changeItemInHand(ServerPlayer player, ItemStack newItem){
        ItemStack oldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, newItem);
        if (!player.getInventory().add(oldItem)) {
            ItemEntity entity = player.drop(oldItem, false);
            if (entity != null) {
                entity.setNoPickUpDelay();
                entity.setTarget(player.getUUID());
            }
        }
        else player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.MASTER, 1, 1);
    }
    public static void giveItem(ServerPlayer player, ItemStack newItem){
        if (!player.getInventory().add(newItem)) {
            ItemEntity entity = player.drop(newItem, false);
            if (entity != null) {
                entity.setNoPickUpDelay();
                entity.setTarget(player.getUUID());
            }
        }
        else player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.MASTER, 1, 1);
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

    public static boolean isBlockInTag(Level level, BlockPos pos, TagKey<Block> tag) {
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
                mc.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
            }
        }
    }
}
