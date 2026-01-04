package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.data.ModTags;
import com.a3ot.eventhorizon.data.ModDamageTypes;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
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
            if (Utils.isBlockPosHasTag(level, pos, ModTags.Blocks.BOTANOPHOBIA)) {
                DamageSource botanophobiaDamage = level.damageSources().source(ModDamageTypes.BOTANOPHOBIA);
                player.hurt(botanophobiaDamage, 2.0F);
                break;
            }
        }
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level){
        setPlatform(player, (ServerLevel) level);
    }

    public static void setPlatform(Player player, ServerLevel level) {
        BlockPos playerPos = player.blockPosition();
        int x = playerPos.getX();
        int z = playerPos.getZ();
        int y = playerPos.getY();

        BlockPos corner1Check = new BlockPos(x - 2, y - 2, z - 2);
        BlockPos corner2Check = new BlockPos(x + 2, y - 1, z + 2);

        boolean hasBotanophobiaBlock = BlockPos.betweenClosedStream(corner1Check, corner2Check)
                .anyMatch(pos -> Utils.isBlockPosHasTag(level, pos, ModTags.Blocks.BOTANOPHOBIA));

        if (hasBotanophobiaBlock) {
            BlockPos corner1Target = new BlockPos(x - 2, y - 1, z - 2);
            Utils.blockFilling(level, corner1Target, corner2Check, Blocks.TERRACOTTA.defaultBlockState());
        }
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
