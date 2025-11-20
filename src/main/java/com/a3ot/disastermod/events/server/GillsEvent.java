package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.subclasses.AbstractAttributeEvent;
import com.a3ot.disastermod.events.subclasses.AbstractPlayerAttributeEvent;
import com.a3ot.disastermod.events.utils.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

public class GillsEvent extends AbstractPlayerAttributeEvent implements IActiveStateEvent {
    private static final ResourceLocation GILLS_ID = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "gills");
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
    protected void defineModifiers() {
        modifiers.put(Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(GILLS_ID, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @Override
    public void onStart(ServerLevel level) {
        this.setActive();
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            MobEffectInstance water_breathing = new MobEffectInstance(MobEffects.WATER_BREATHING, Math.min(Math.max(ServerTick.getTotalTicks() / 2, 200), 1200), 0, false, true);
            player.addEffect(water_breathing);
        });
        super.onStart(level);
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void drowning(LivingBreatheEvent event){
        if(!GillsEvent.active) return;
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        boolean isUnderwater = player.isUnderWater();
        boolean hasWaterBreathing = player.hasEffect(MobEffects.WATER_BREATHING);
        event.setCanBreathe(isUnderwater || hasWaterBreathing);
        if (isUnderwater || hasWaterBreathing) {
            event.setCanBreathe(true);
            event.setRefillAirAmount(6);
            event.setConsumeAirAmount(-1);
        } else {
            event.setCanBreathe(false);
            event.setRefillAirAmount(-1);
            event.setConsumeAirAmount(1);
        }
    }

    public static void playerTick(Player player) {
        if (!active) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!Utils.isValidPlayer(serverPlayer)) return;
        if (!player.isInWater()) return;
        MobEffectInstance dolphins_grace = new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 200, 0, false, false);
        player.addEffect(dolphins_grace);
        MobEffectInstance conduit_power = new MobEffectInstance(MobEffects.CONDUIT_POWER, 200, 1, false, false);
        player.addEffect(conduit_power);
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level){
        if(!(Utils.isValidPlayer(player))) return;
        MobEffectInstance water_breathing = new MobEffectInstance(MobEffects.WATER_BREATHING, Math.min(Math.max(ServerTick.getTotalTicks() / 2, 200), 1200), 0, false, true);
        player.addEffect(water_breathing);
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof EnderBloodEvent;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 2.5;
    }
}
