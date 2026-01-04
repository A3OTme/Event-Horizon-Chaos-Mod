package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin {
    @Unique
    private static final List<SoundEvent> eventhorizon$ALL_SOUND_EVENTS = BuiltInRegistries.SOUND_EVENT
            .stream()
            .filter(soundEvent -> {
                ResourceLocation location = soundEvent.getLocation();
                String path = location.getPath().toLowerCase();
                return !path.contains("ambient") && !path.contains("music");
            })
            .toList();

    @Redirect(
            method = "resolve(Lnet/minecraft/client/sounds/SoundManager;)Lnet/minecraft/client/sounds/WeighedSoundEvents;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/WeighedSoundEvents;getSound(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/client/resources/sounds/Sound;"
            )
    )
    private Sound eventhorizon$redirectGetSound(WeighedSoundEvents instance, RandomSource randomSource) {
        if (ClientVariables.randomSounds && !eventhorizon$ALL_SOUND_EVENTS.isEmpty()) {
                SoundEvent randomSoundEvent = eventhorizon$ALL_SOUND_EVENTS.get(ThreadLocalRandom.current().nextInt(eventhorizon$ALL_SOUND_EVENTS.size()));
                try {
                    Minecraft mc = Minecraft.getInstance();
                    SoundManager soundManager = mc.getSoundManager();
                    WeighedSoundEvents randomWeightedSounds = soundManager.getSoundEvent(randomSoundEvent.getLocation());
                    if (randomWeightedSounds != null) {
                        return randomWeightedSounds.getSound(randomSource);
                    }
                } catch (Exception ignored) {}
                return instance.getSound(randomSource);
        }
        return instance.getSound(randomSource);
    }
}
