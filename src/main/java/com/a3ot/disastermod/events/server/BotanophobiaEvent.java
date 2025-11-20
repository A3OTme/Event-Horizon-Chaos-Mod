package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.ModTags;
import com.a3ot.disastermod.data.ModDamageTypes;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BotanophobiaEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;

    @Override
    public void setActive() {
        active = true;
    }

    @Override
    public void setInactive() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            setPlatform(player,level);
        });
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void playerTick(Player player, Level level){
        if(!active) return;
        if(!Utils.isValidPlayer(player)) return;
        BlockPos entityPos = player.blockPosition();
        java.util.List<BlockPos> checkPositions = new java.util.ArrayList<>(2);
        checkPositions.add(entityPos);
        if (player.onGround()) {
            checkPositions.add(entityPos.below());
        }
        for (BlockPos pos : checkPositions) {
            if (Utils.isBlockInTag(level, pos, ModTags.Blocks.BOTANOPHOBIA)) {
                DamageSource botanophobiaDamage = level.damageSources().source(ModDamageTypes.BOTANOPHOBIA);
                player.hurt(botanophobiaDamage, 2.0F);
                break;
            }
        }
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level){
        setPlatform(player,level);
    }

    public static void setPlatform(Player player, Level level){
        if(!(Utils.isValidPlayer(player))) return;
        BlockPos playerPos = player.blockPosition();
        int x = playerPos.getX();
        int z = playerPos.getZ();
        int y = playerPos.getY();
        BlockPos.betweenClosedStream(
                        new BlockPos(x - 2, y - 1, z - 2),
                        new BlockPos(x + 2, y - 1, z + 2))
                .forEach(pos -> {
                    BlockState state = level.getBlockState(pos);
                    if (state.is(ModTags.Blocks.IMPORTANT_BLOCKS) || state.isEmpty() || !state.is(ModTags.Blocks.BOTANOPHOBIA)) return;
                    if (state.is(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS)) level.destroyBlock(pos, true);
                    else level.setBlock(pos, Blocks.TERRACOTTA.defaultBlockState(), 3);
                });
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GREEN;
    }

    public float getPitch() {
        return 1.5F;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.THORNS_HIT;
    }
}
