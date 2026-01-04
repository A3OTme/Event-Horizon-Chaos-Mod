package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.events.client.ShakyCrosshairEvent;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
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
    private final ShakyCrosshairEvent eventhorizon$tcManager = ShakyCrosshairEvent.getInstance();

    @Inject(
            method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",
                    shift = At.Shift.AFTER
            )
    )
    private void eventhorizon$beforeRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (ClientVariables.shakyCrosshair) {
            eventhorizon$tcManager.updateOffset();
            PoseStack poseStack = guiGraphics.pose();
            poseStack.translate(eventhorizon$tcManager.getOffsetX(), eventhorizon$tcManager.getOffsetY(), 0.0f);

        } else {
            eventhorizon$tcManager.resetOffset();
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
    private void eventhorizon$afterRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (eventhorizon$tcManager.isOffsetAppliedThisFrame()) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.translate(-eventhorizon$tcManager.getOffsetX(), -eventhorizon$tcManager.getOffsetY(), 0.0f);
        }
    }

    @Inject(
            method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At("TAIL")
    )
    private void eventhorizon$onRenderCrosshairTail(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        eventhorizon$tcManager.onFrameEnd();
    }
}