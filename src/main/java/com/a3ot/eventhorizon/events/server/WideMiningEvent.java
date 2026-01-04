package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WideMiningEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

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
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void breakBlock(Player player, BlockPos initialBlockPos, LevelAccessor level){
        if (!WideMiningEvent.active) return;

        if (player.isCrouching()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof DiggerItem item)) return;

        if (HARVESTED_BLOCKS.contains(initialBlockPos)) return;
        for (BlockPos pos : WideMiningEvent.getBlocksToBeDestroyed(initialBlockPos, serverPlayer)) {
            if (!item.isCorrectToolForDrops(mainHandItem, level.getBlockState(pos))) continue;
            HARVESTED_BLOCKS.add(pos);
            serverPlayer.gameMode.destroyBlock(pos);
            HARVESTED_BLOCKS.remove(pos);
        }
    }

    private static List<BlockPos> getBlocksToBeDestroyed(BlockPos initialBlockPos, ServerPlayer player) {
        List<BlockPos> positions = new ArrayList<>();

        BlockHitResult traceResult = player.level().clip(new ClipContext(
                player.getEyePosition(1f),
                player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
        ));
        if (traceResult.getType() == HitResult.Type.MISS) return positions;

        Direction direction = traceResult.getDirection();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                BlockPos pos;
                if (direction == Direction.DOWN || direction == Direction.UP) {
                    pos = new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY(), initialBlockPos.getZ() + y);
                } else if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    pos = new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY() + y, initialBlockPos.getZ());
                } else {
                    pos = new BlockPos(initialBlockPos.getX(), initialBlockPos.getY() + y, initialBlockPos.getZ() + x);
                }
                if (!pos.equals(initialBlockPos)) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GOLD;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.BEACON_POWER_SELECT;
    }
}
