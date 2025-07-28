package com.a3ot.disastermod.mixin;

import com.a3ot.disastermod.events.server.PetCarrierEvent;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public class CatMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void disastermod$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Cat cat = (Cat) (Object) this;
        if (PetCarrierEvent.isPetCarrierCat(cat)) {
            if (!player.isSteppingCarefully()) {
                PetCarrierEvent.openOwnerEnderChest(cat, player);
                cir.cancel();
                cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide));
            }
        }
    }
}