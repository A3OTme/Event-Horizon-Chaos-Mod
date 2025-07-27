package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import com.a3ot.disastermod.network.NetworkHandler;
import com.a3ot.disastermod.network.packet.ItemActivationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

import static net.neoforged.neoforge.common.CommonHooks.onLivingUseTotem;

public class AllItemsAreTotemEvent implements AbstractEvent, IActiveStateEvent {
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
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static boolean livingDeath(LivingEntity entity, DamageSource damageSource){
        if (!active) return false;
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        if (!(entity instanceof ServerPlayer player)) return false;
        if (!Utils.isValidDeadPlayer(player)) return false;
        ItemStack usedStack = null;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty() && onLivingUseTotem(player, damageSource, stack, hand)) {
                usedStack = stack.copy();
                stack.setCount(0);
                break;
            }
        }
        if (usedStack != null) {
            player.setHealth(4.0F);
            NetworkHandler.sendToClient(player, new ItemActivationPacket(usedStack, player.getId()));
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(usedStack.getItem()), 1);
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }
            return true;
        }
        return false;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}