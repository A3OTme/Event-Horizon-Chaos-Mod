package com.a3ot.eventhorizon.mixin;

import com.a3ot.eventhorizon.api.event.PlayerChangeGameModeEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "setGameMode(Lnet/minecraft/world/level/GameType;)Z", at = @At("RETURN"))
    private void postGameModeChange(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            GameType oldGameMode = player.gameMode.getPreviousGameModeForPlayer();
            GameType currentNewMode = player.gameMode.getGameModeForPlayer();
            NeoForge.EVENT_BUS.post(new PlayerChangeGameModeEvent.Post(player, oldGameMode, currentNewMode));
        }
    }
}
