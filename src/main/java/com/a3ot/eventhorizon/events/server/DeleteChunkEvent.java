package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.data.ModTags;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.*;

public class DeleteChunkEvent implements AbstractEvent {
    private static final int BLOCKS_PER_TICK = 500;

    @Override
    public void onStart(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (!Utils.isValidPlayer(player)) continue;
            int playerX = (int) player.getX();
            int playerZ = (int) player.getZ();
            int minX = playerX - 8;
            int maxX = playerX + 7;
            int minZ = playerZ - 8;
            int maxZ = playerZ + 7;
            List<BlockPos> blocks = new ArrayList<>();
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    for (int y = 319; y >= -64; y--) {
                        BlockPos pos = new BlockPos(x, y, z);
                        blocks.add(pos);
                    }
                }
            }
            processBlocksAsync(level, blocks, 0);
            MobEffectInstance slow_falling = new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false);
            player.addEffect(slow_falling);
        }
    }

    private void processBlocksAsync(Level level, List<BlockPos> blocks, int startIndex) {
        int processed = 0;
        int index = startIndex;

        while (index < blocks.size() && processed < BLOCKS_PER_TICK) {
            BlockPos pos = blocks.get(index);
            BlockState state = level.getBlockState(pos);

            if (state.is(ModTags.Blocks.IMPORTANT_BLOCKS) || state.isAir()) {
                index++;
                processed++;
                continue;
            }

            if (state.is(Tags.Blocks.STORAGE_BLOCKS)) {
                level.destroyBlock(pos, true);
            } else {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }

            index++;
            processed++;
        }

        if (index < blocks.size()) {
            int finalIndex = index;
            Objects.requireNonNull(level.getServer()).submit(() -> processBlocksAsync(level, blocks, finalIndex));
        }
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}
