package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.server.WideMiningEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = Disastermod.MODID)
public class BlockHandler {

    @SubscribeEvent
    public static void onBreakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos initialBlockPos = event.getPos();
        LevelAccessor level = event.getLevel();
        WideMiningEvent.breakBlock(player, initialBlockPos, level);
    }
}
