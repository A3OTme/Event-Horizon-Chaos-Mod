package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RottingFoodEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;
    private static final Item rottenFlesh = Items.ROTTEN_FLESH;

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

    public static void livingDamage(LivingEntity entity, @Nullable DamageSource damageSource) {
        if (!active || damageSource == null) return;
        if (!(entity instanceof ServerPlayer player)) return;
        if (!Utils.isPlayerValid(player)) return;

        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker) || !livingAttacker.getType().is(EntityTypeTags.UNDEAD)) {
            return;
        }

        NonNullList<ItemStack> items = player.getInventory().items;
        List<Integer> foodIndices = new ArrayList<>();
        ItemStack offhandStack = player.getOffhandItem();
        if (!offhandStack.isEmpty() && offhandStack.has(DataComponents.FOOD) && !offhandStack.is(rottenFlesh)) {
            foodIndices.add(40);
        }
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty() && stack.has(DataComponents.FOOD) && !stack.is(rottenFlesh)) {
                foodIndices.add(i);
            }
        }
        if (foodIndices.isEmpty()) return;

        Collections.shuffle(foodIndices);
        int index = foodIndices.getFirst();
        if (index == 40) player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(rottenFlesh));
        else items.set(index, new ItemStack(rottenFlesh));
        player.getInventory().setChanged();
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GREEN;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.COMPOSTER_EMPTY;
    }
}
