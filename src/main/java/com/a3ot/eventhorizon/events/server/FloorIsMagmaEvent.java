package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FloorIsMagmaEvent implements AbstractEvent, IActiveStateEvent {
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
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            MobEffectInstance fire_resistance = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, true);
            player.addEffect(fire_resistance);
        });
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void playerTick(Player player, Level level){
        if (!active) return;
        if (!Utils.isValidPlayer(player)) return;
        if (!player.onGround()) return;
        BlockPos posBelow = player.blockPosition().below();
        BlockState state = level.getBlockState(posBelow);
        if (state.is(Blocks.MAGMA_BLOCK)
                || Utils.isBlockPosHasTag(level, posBelow, BlockTags.CAMPFIRES)
                || state.isEmpty()) return;
        Registry<Enchantment> registry = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> frostWalkerHolder = registry.getHolderOrThrow(Enchantments.FROST_WALKER);
        int frostWalkerLevel = EnchantmentHelper.getEnchantmentLevel(frostWalkerHolder, player);
        boolean hasFireResistance = player.hasEffect(MobEffects.FIRE_RESISTANCE);
        if (frostWalkerLevel > 0 || hasFireResistance) return;
        if (!player.isSteppingCarefully()) player.hurt(player.damageSources().hotFloor(), 1);
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level){
        if(!(Utils.isValidPlayer(player))) return;
        MobEffectInstance fire_resistance = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, true);
        player.addEffect(fire_resistance);
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_RED;
    }

    public float getPitch() {
        return 0.5F;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.PLAYER_HURT_ON_FIRE;
    }
}
