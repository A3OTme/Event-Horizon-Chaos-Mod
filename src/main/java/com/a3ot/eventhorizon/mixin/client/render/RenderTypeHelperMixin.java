package com.a3ot.eventhorizon.mixin.client.render;

import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.RenderTypeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTypeHelper.class)
public class RenderTypeHelperMixin {
    @Inject(
            method = "getEntityRenderType(Lnet/minecraft/client/renderer/RenderType;Z)Lnet/minecraft/client/renderer/RenderType;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void eventhorizon$onGetEntityRenderType(RenderType chunkRenderType, boolean cull, CallbackInfoReturnable<RenderType> cir) {
        if (ClientVariables.noTransparency) cir.setReturnValue(Sheets.solidBlockSheet());
    }

    @Inject(
            method = "getMovingBlockRenderType",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void eventhorizon$onGetMovingBlockRenderType(RenderType renderType, CallbackInfoReturnable<RenderType> cir) {
        if (ClientVariables.noTransparency) cir.setReturnValue(Sheets.solidBlockSheet());
    }

    @Inject(
            method = "getFallbackItemRenderType",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void eventhorizon$onGetFallbackItemRenderType(ItemStack stack, BakedModel model, boolean cull, CallbackInfoReturnable<RenderType> cir) {
        if (ClientVariables.noTransparency) cir.setReturnValue(Sheets.solidBlockSheet());
    }
}
