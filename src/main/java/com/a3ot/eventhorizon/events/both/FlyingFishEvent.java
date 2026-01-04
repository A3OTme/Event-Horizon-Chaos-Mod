package com.a3ot.eventhorizon.events.both;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.ServerTick;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.events.server.EnderBloodEvent;
import com.a3ot.eventhorizon.events.subclasses.AbstractPlayerAttributeEvent;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FlyingFishEvent extends AbstractPlayerAttributeEvent implements IActiveStateEvent {
    private static final ResourceLocation GILLS_ID = ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "flying_fish");
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
    public void onClientStart(Level level) {
        ClientVariables.flyingFish = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.flyingFish = false;
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

    public static void playerTick(Player player) {
        if (!active) return;
        if (player.isEyeInFluid(FluidTags.WATER)) return; //water is air!
        if (!ClientVariables.flyingFish) return;
        MobEffectInstance dolphins_grace = new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 30, 0, false, false);
        player.addEffect(dolphins_grace);
        MobEffectInstance conduit_power = new MobEffectInstance(MobEffects.CONDUIT_POWER, 30, 1, false, false);
        player.addEffect(conduit_power);
        MobEffectInstance water_breathing = new MobEffectInstance(MobEffects.WATER_BREATHING, 30, 0, false, true);
        player.addEffect(water_breathing);
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

    @Override
    public EventSide getSide() {
        return EventSide.BOTH;
    }
}
