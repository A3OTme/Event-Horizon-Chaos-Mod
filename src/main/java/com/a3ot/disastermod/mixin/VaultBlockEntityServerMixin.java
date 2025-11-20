package com.a3ot.disastermod.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VaultBlockEntity.Server.class)
public class VaultBlockEntityServerMixin {

    @Redirect(
            method = "isValidToInsert",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private static boolean redirectIsSameItemSameComponents(ItemStack stack, ItemStack keyItem) {
        return ItemStack.isSameItem(stack, keyItem) && stack.getCount() >= keyItem.getCount();
    }
}