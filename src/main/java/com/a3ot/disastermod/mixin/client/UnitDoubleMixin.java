package com.a3ot.disastermod.mixin.client;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(OptionInstance.UnitDouble.class)
public class UnitDoubleMixin {
    @Inject(
            method = "validateValue(Ljava/lang/Double;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void bypassRangeValidation(Double value, CallbackInfoReturnable<Optional<Double>> cir) {
        cir.setReturnValue(Optional.ofNullable(value));
    }
}
