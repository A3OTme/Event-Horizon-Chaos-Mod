package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.utils.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class BonemealOnSneakEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;
    private static final Map<Player, Boolean> wasCrouchingLastTick = new HashMap<>();

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

    public static void playerTick(Player player, Level level) {
        if (!active) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!Utils.isValidPlayer(serverPlayer)) return;
        if (!player.onGround()) return;
        boolean crouching = player.isCrouching();
        boolean wasCrouching = wasCrouchingLastTick.getOrDefault(player, false);
        if (!wasCrouching && crouching) {
            BlockPos pos = serverPlayer.getOnPos();
            if (BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, pos.above(), serverPlayer) ||
                    BoneMealItem.growWaterPlant(ItemStack.EMPTY, level, pos.above(), Direction.DOWN)) {
                level.levelEvent(1505, pos.above(), 0);
            } else if (BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, pos, serverPlayer)) {
                level.levelEvent(1505, pos, 0);
            } else if (BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, serverPlayer.blockPosition(), serverPlayer)) {
                level.levelEvent(1505, serverPlayer.blockPosition(), 0);
            }
        }
        wasCrouchingLastTick.put(player, crouching);
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}