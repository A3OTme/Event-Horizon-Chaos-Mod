package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.ModTags;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class DeleteChunkEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        Set<BlockPos> processedPositions = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            if (!Utils.isValidPlayer(player)) continue;
            int playerX = (int) player.getX();
            int playerZ = (int) player.getZ();
            int minX = playerX - 8;
            int maxX = playerX + 7;
            int minZ = playerZ - 8;
            int maxZ = playerZ + 7;
            BlockPos.betweenClosedStream(minX, -64, minZ, maxX, 319, maxZ)
                    .map(BlockPos::immutable)
                    .sorted(Comparator.comparingInt((BlockPos pos) -> pos.getY()).reversed())
                    .forEach(pos -> {
                if (processedPositions.add(pos.immutable())) {
                    BlockState state = level.getBlockState(pos);
                    if (state.is(ModTags.Blocks.IMPORTANT_BLOCKS) || state.isAir()) return;
                    if (state.is(net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS)) level.destroyBlock(pos, true);
                    else level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                }
            });
            MobEffectInstance slow_falling = new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false);
            player.addEffect(slow_falling);
        }
        processedPositions.clear();
    }
}
