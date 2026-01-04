package com.a3ot.eventhorizon.mixin;

import com.a3ot.eventhorizon.events.server.EnchantHoneyBottleEvent;
import com.a3ot.eventhorizon.registry.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BeehiveBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {

    @Redirect(
            method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void onSetItemInHand(Player player, InteractionHand hand, ItemStack stack) {
        if (EnchantHoneyBottleEvent.active && stack.is(Items.HONEY_BOTTLE)) {
            player.setItemInHand(hand, ModItems.ENCHANTED_HONEY_BOTTLE.toStack());
        } else {
            player.setItemInHand(hand, stack);
        }
    }

    @Redirect(
            method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean onInventoryAdd(Inventory instance, ItemStack stack) {
        if (EnchantHoneyBottleEvent.active && stack.is(Items.HONEY_BOTTLE)) {
            return instance.add(ModItems.ENCHANTED_HONEY_BOTTLE.toStack());
        } else {
            return instance.add(stack);
        }
    }

    @Redirect(
            method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private ItemEntity onPlayerDrop(Player player, ItemStack stack, boolean z) {
        if (EnchantHoneyBottleEvent.active && stack.is(Items.HONEY_BOTTLE)) {
            return player.drop(ModItems.ENCHANTED_HONEY_BOTTLE.toStack(), z);
        } else {
            return player.drop(stack, z);
        }
    }
}
