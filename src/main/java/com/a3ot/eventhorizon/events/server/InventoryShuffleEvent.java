package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import com.a3ot.eventhorizon.network.NetworkHandler;
import com.a3ot.eventhorizon.network.packet.ShuffleRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collections;

public class InventoryShuffleEvent implements AbstractEvent, IActiveStateEvent{
    private static boolean active = false;

    @Override
    public void setActive() {
        active = true;
    }

    @Override
    public void setInactive() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onStart(ServerLevel level) {
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleKeybinds() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.options.keyInventory.consumeClick()) {
            if (Utils.isValidPlayer(mc.player)) {
                NetworkHandler.sendToServer(new ShuffleRequestPacket());
            }
            mc.setScreen(new InventoryScreen(mc.player));
        }
    }

    public static void shuffle(Player player){
        if (!active) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        NonNullList<ItemStack> items = serverPlayer.getInventory().items;
        if (items.isEmpty()) return;
        Collections.shuffle(items);
        serverPlayer.getInventory().setChanged();
        serverPlayer.playNotifySound(SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.MASTER, 1, 1);
    }
}
