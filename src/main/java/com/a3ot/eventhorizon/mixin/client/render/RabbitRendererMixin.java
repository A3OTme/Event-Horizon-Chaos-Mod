package com.a3ot.eventhorizon.mixin.client.render;

import com.a3ot.eventhorizon.data.CustomRabbitVariant;
import com.a3ot.eventhorizon.data.ModDataComponents;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitRenderer.class)
public class RabbitRendererMixin {

    @Inject(
            method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Rabbit;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectGetTextureLocation(Rabbit rabbit, CallbackInfoReturnable<ResourceLocation> cir) {
        CustomRabbitVariant customVariant = rabbit.getExistingDataOrNull(ModDataComponents.CUSTOM_RABBIT_VARIANT.get());
        if (customVariant != null) cir.setReturnValue(customVariant.textureLocation());
    }
}