package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.data.DeathStorage;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ReturnToLastDeathEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        List<ServerPlayer> playersCopy = new ArrayList<>(level.players().stream().filter(Utils::isValidPlayer).toList());
        playersCopy.forEach(player -> {
            DeathStorage.DeathData deathData = DeathStorage.getDeathData(player.getUUID());
            if (deathData != null) {
                teleportPlayer(player, deathData);
            } else {
                giveTotem(player);
            }
        });
    }

    private void teleportPlayer(ServerPlayer player, DeathStorage.DeathData deathData) {
        ServerLevel targetLevel = Objects.requireNonNull(player.getServer()).getLevel(deathData.dimension);
        if (targetLevel != null) {
            BlockPos pos = deathData.pos;
            Vec3 position = Vec3.atBottomCenterOf(pos);
            DimensionTransition.PostDimensionTransition postTransition = DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET);
            DimensionTransition transition = new DimensionTransition(
                targetLevel, position, player.getDeltaMovement(), player.getYRot(), player.getXRot(), false, postTransition
            );
            player.changeDimension(transition);
        }
    }

    private void giveTotem(ServerPlayer player) {
        ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
        totem.set(DataComponents.LORE, new ItemLore(
            List.of(
                Component.translatable("disastermod.configuration.returnToLastDeath.totem_of_undying")
                        .withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.GRAY))
            )
        ));
        Utils.giveItem(player, totem);
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}