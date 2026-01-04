package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.Font$StringRenderOutput")
public class FontStringRenderOutputMixin {
    @WrapOperation(
            method = "accept",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Style;isObfuscated()Z"
            )
    )
    private boolean modifyObfuscated(Style instance, Operation<Boolean> original) {
        if (ClientVariables.obfuscate) return true;
        if (ClientVariables.cuteFont) return false;
        return original.call(instance);
    }
}
