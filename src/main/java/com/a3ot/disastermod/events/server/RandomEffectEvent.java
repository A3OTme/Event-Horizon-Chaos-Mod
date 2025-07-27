package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class RandomEffectEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        Registry<MobEffect> mobEffectsRegistry = level.registryAccess().registryOrThrow(Registries.MOB_EFFECT);
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            for (int i = 0; i < 3; i++) {
                Holder<MobEffect> randomEffectHolder = mobEffectsRegistry.getRandom(level.random).orElseThrow();
                MobEffect effect = randomEffectHolder.value();
                int duration = effect.isInstantenous() ? 1 : 1200;
                MobEffectInstance effectInstance = new MobEffectInstance(randomEffectHolder, duration, 1, false, true);
                player.addEffect(effectInstance);
            }
        });
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GREEN;
    }
}
