package com.a3ot.eventhorizon.mixin;

import com.a3ot.eventhorizon.data.ModDataComponents;
import com.a3ot.eventhorizon.handlers.RabbitJumpCounterHandler;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Rabbit.class)
public class RabbitMixin {

    @Inject(method = "startJumping", at = @At("HEAD"))
    private void onRabbitStartJumping(CallbackInfo ci) {
        Rabbit rabbit = (Rabbit) (Object) this;
        if (rabbit.hasData(ModDataComponents.CUSTOM_RABBIT_VARIANT)) RabbitJumpCounterHandler.incrementJumpCount(rabbit);
    }
}