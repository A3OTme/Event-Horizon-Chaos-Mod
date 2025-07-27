package com.a3ot.disastermod.mixin.client.render;

import com.a3ot.disastermod.events.utils.Utils;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
                    shift = Shift.AFTER,
                    ordinal = 1))
    public void flatterRender(T entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, CallbackInfo cir) {
        if (ClientVariables.wideMobs) {
            if (entityIn instanceof Player player) {
                if (Utils.isValidPlayer(player)) poseStack.scale(3, 1, 3);
            }
            else poseStack.scale(3, 1, 3);
        }
    }
}
