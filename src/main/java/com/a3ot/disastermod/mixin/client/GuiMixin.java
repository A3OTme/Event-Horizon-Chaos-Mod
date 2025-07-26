package com.a3ot.disastermod.mixin.client;

import com.a3ot.disastermod.events.client.ShakingCrosshairEvent;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Unique
    private final ShakingCrosshairEvent disastermod$tcManager = ShakingCrosshairEvent.getInstance();

    @Inject(
            method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",
                    shift = At.Shift.AFTER
            )
    )
    private void disastermod$beforeRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (ClientVariables.tremblingCrosshair) {
            disastermod$tcManager.updateOffset();
            PoseStack poseStack = guiGraphics.pose();
            poseStack.translate(disastermod$tcManager.getOffsetX(), disastermod$tcManager.getOffsetY(), 0.0f);

        } else {
            disastermod$tcManager.resetOffset();
        }
    }

    @Inject(
            method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void disastermod$afterRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (disastermod$tcManager.isOffsetAppliedThisFrame()) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.translate(-disastermod$tcManager.getOffsetX(), -disastermod$tcManager.getOffsetY(), 0.0f);
        }
    }

    @Inject(
            method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At("TAIL")
    )
    private void disastermod$onRenderCrosshairTail(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        disastermod$tcManager.onFrameEnd();
    }
}