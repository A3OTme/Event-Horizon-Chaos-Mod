package com.a3ot.disastermod.mixin.client.render;

import com.a3ot.disastermod.handlers.client.ClientVariables;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {

    @Inject(method = "getRenderLayer(Lnet/minecraft/world/level/material/FluidState;)Lnet/minecraft/client/renderer/RenderType;", at = @At("HEAD"), cancellable = true)
    private static void disastermod$onGetRenderLayer(FluidState fluidState, CallbackInfoReturnable<RenderType> cir) {
        if (ClientVariables.noTransparency) cir.setReturnValue(RenderType.solid());
    }
    @Inject(method = "getRenderLayers(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/neoforged/neoforge/client/ChunkRenderTypeSet;", at = @At("HEAD"), cancellable = true)
    private static void disastermod$onGetRenderLayers(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        if (ClientVariables.noTransparency) cir.setReturnValue(ChunkRenderTypeSet.of(RenderType.solid()));
    }
}
