package com.a3ot.eventhorizon.mixin.client;

import com.a3ot.eventhorizon.events.client.NoInventoryEvent;
import com.a3ot.eventhorizon.events.client.SmoothCameraEvent;
import com.a3ot.eventhorizon.events.server.InventoryShuffleEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void onHandleKeybinds(CallbackInfo ci) {
        if (NoInventoryEvent.handleKeybinds()) ci.cancel();
        if (SmoothCameraEvent.handleKeybinds()) ci.cancel();
        InventoryShuffleEvent.handleKeybinds();
    }
}
