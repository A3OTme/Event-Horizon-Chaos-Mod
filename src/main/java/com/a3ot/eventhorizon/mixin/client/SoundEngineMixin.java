package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@OnlyIn(Dist.CLIENT)
@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Redirect(
            method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
            )
    )
    private float modifyVolume(float value, float min, float max, float volumeMultiplier, SoundSource source) {
        if (ClientVariables.muted) return 0.0F;
        return Mth.clamp(volumeMultiplier * ((SoundEngine) (Object) this).getVolume(source), 0.0F, 1.0F);
    }

    @Redirect(
            method = "calculatePitch(Lnet/minecraft/client/resources/sounds/SoundInstance;)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;clamp(FFF)F"
            )
    )
    private float modifyPitch(float value, float min, float max, SoundInstance sound) {
        if (ClientVariables.pitchMax) return 2.0F;
        if (ClientVariables.pitchMin) return 0.5F;
        return Mth.clamp(value, min, max);
    }
}
