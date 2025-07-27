package com.a3ot.disastermod.handlers;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.api.event.FluidCollisionEvent;
import com.a3ot.disastermod.data.DeathStorage;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.server.InventoryShuffleEvent;
import com.a3ot.disastermod.events.server.*;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Disastermod.MODID)
public class PlayerHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide()) return;

        DementiaEvent.playerTick(player);
        ScopophobiaEvent.playerTick(player);
        HydrophobiaEvent.playerTick(player, level);
        BotanophobiaEvent.playerTick(player, level);
        FloorIsMagmaEvent.playerTick(player, level);
        NyctophobiaEvent.playerTick(player, level);
        BlindRageEvent.playerTick(player, level);
    }

    @SubscribeEvent
    public static void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (!Utils.isPlayerValid(player)) return;
        InventoryShuffleEvent.shuffle(player);
    }

    @SubscribeEvent
    public static void onFluidCollide(FluidCollisionEvent event) {
        LivingEntity entity = event.getEntity();
        FluidState fluid = event.getFluid();
        event.setCanceled(FluidWalkerEvent.FluidCollision(entity, fluid));
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();
        RottingFoodEvent.livingDamage(entity, damageSource);
        EnderBloodEvent.livingDamage(entity);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();
        event.setCanceled(AllItemsAreTotemEvent.livingDeath(entity, damageSource));
        CurseOfVanishingEvent.livingDeath(entity);
        DementiaEvent.livingDeath(entity);
        DeathStorage.recordPlayerDeath(entity);
    }

    @SubscribeEvent
    public static void onDrowning(LivingBreatheEvent event) {
        GillsEvent.Drowning(event);
    }
}