package com.a3ot.eventhorizon.mixin;

import com.a3ot.eventhorizon.events.utils.Utils;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Redirect(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Item;damageItem(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)I"
            )
    )
    private int redirectDamageItemCall(Item instance, ItemStack itemStack, int i, LivingEntity entity, Consumer<Item> consumer) {
        int modifiedDamage = i;
        if (entity instanceof Player player) {
            if (ClientVariables.negligentUse && Utils.isValidPlayer(player)) {
                modifiedDamage = i * 10;
            }
        }
        return instance.damageItem(itemStack, modifiedDamage, entity, consumer);
    }
}