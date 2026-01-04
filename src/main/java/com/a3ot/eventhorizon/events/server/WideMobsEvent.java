package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import com.a3ot.eventhorizon.network.NetworkHandler;
import com.a3ot.eventhorizon.network.packet.ClientVariableSyncPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class WideMobsEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        level.players().forEach(player -> NetworkHandler.sendToClient(player, new ClientVariableSyncPacket("wideMobs", true)));
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.players().forEach(player -> NetworkHandler.sendToClient(player, new ClientVariableSyncPacket("wideMobs", false)));
    }

    @Override
    public void playerRespawnOrJoin(Player player, Level level) {
        NetworkHandler.sendToClient((ServerPlayer) player, new ClientVariableSyncPacket("wideMobs", true));
    }

    public static void modifyHitbox(CallbackInfoReturnable<AABB> cir, Entity entity){
        if (ClientVariables.wideMobs && (
                (entity instanceof Player player && Utils.isValidPlayer(player)) ||
                (entity instanceof LivingEntity && !(entity instanceof Player)) ||
                entity instanceof Boat ||
                entity instanceof Minecart ||
                entity instanceof EnderDragon ||
                entity instanceof EndCrystal)) {
            AABB original = cir.getReturnValue();
            AABB expanded = original.inflate(original.getXsize(), 0.0, original.getZsize());
            cir.setReturnValue(expanded);
        }
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_AQUA;
    }
}
