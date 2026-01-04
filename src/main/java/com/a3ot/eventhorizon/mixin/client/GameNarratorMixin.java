package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameNarrator.class)
public abstract class GameNarratorMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "updateNarratorStatus", at = @At("HEAD"), cancellable = true)
    private void turnOn(NarratorStatus mode, CallbackInfo ci) {
        if (ClientVariables.forceNarrator && mode != NarratorStatus.ALL) {
            minecraft.options.narrator().set(NarratorStatus.ALL);
            ci.cancel();
        }
    }
}