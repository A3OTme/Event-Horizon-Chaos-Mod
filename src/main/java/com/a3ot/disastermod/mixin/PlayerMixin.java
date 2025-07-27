package com.a3ot.disastermod.mixin;

import com.a3ot.disastermod.events.utils.Utils;
import com.a3ot.disastermod.handlers.client.ClientVariables;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    private void modifyPlayerPose(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (ClientVariables.onlySwimmingActive && Utils.isValidPlayer(player)) {
            player.setForcedPose(null);
            player.setPose(Pose.SWIMMING);
            ci.cancel();
        }
    }
}
