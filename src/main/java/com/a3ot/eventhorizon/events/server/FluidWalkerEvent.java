package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class FluidWalkerEvent implements AbstractEvent {
    @Override
    public void onClientStart(Level level) {
        ClientVariables.fluidWalker = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.fluidWalker = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    public static boolean FluidCollision(LivingEntity entity, FluidState fluid){
        if (!ClientVariables.fluidWalker) return false;
        if (!(entity instanceof Player player)) return false;
        if (!player.isSteppingCarefully()
                && (fluid.is(FluidTags.WATER) || fluid.is(FluidTags.LAVA))) {
            Registry<Enchantment> registry = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> frostWalkerHolder = registry.getHolderOrThrow(Enchantments.FROST_WALKER);
            int frostWalkerLevel = EnchantmentHelper.getEnchantmentLevel(frostWalkerHolder, player);
            if (fluid.is(FluidTags.LAVA) && !player.fireImmune() && frostWalkerLevel < 1 ) {
                player.hurt(player.damageSources().hotFloor(), 1);
            }
            return true;
        }
        return  false;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 3;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}