package com.a3ot.eventhorizon.handlers;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.api.event.FluidCollisionEvent;
import com.a3ot.eventhorizon.data.DeathStorage;
import com.a3ot.eventhorizon.events.both.FlyingFishEvent;
import com.a3ot.eventhorizon.events.server.InventoryShuffleEvent;
import com.a3ot.eventhorizon.events.server.*;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = EventHorizon.MOD_ID)
public class PlayerHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide()) return;

        DementiaEvent.playerTick(player);
        FlyingFishEvent.playerTick(player);
        EnderBloodEvent.playerTick(player, level);
        BotanophobiaEvent.playerTick(player, level);
        FloorIsMagmaEvent.playerTick(player, level);
        NyctophobiaEvent.playerTick(player, level);
        BlindRageEvent.playerTick(player, level);
        BonemealerEvent.playerTick(player, level);
    }

    @SubscribeEvent
    public static void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (!Utils.isValidPlayer(player)) return;
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
        EnderBloodEvent.livingDamage(entity);
        DamageBringsToExplosionEvent.livingDamage(entity, damageSource);
        HuntingSeasonEvent.livingDamage(entity);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();
        event.setCanceled(AllItemsAreTotemEvent.livingDeath(entity, damageSource));
        DementiaEvent.livingDeath(entity);
        DeathStorage.recordPlayerDeath(entity);
    }
}