package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.subclasses.AbstractInventoryEvent;
import com.a3ot.disastermod.events.subclasses.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameRules;

import java.util.Objects;
import java.util.Optional;

public class CurseOfVanishingEvent extends AbstractInventoryEvent implements AbstractInventoryEvent.ILevelAwareEvent, IActiveStateEvent {
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
        super.onStart(level);
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }

    @Override
    public int getInterval() {
        return 200;
    }

    public static void livingDeath(LivingEntity entity) {
        if (!active) return;
        if (!(entity instanceof ServerPlayer player)) return;
        if (!Utils.isPlayerValid(player)) return;
        if (Objects.requireNonNull(player.level().getServer()).getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get()) return;
        player.getInventory().clearContent();
    }

    @Override
    public ItemStack modifyItem(ItemStack stack, ServerLevel level) {
        if (stack.isEmpty() || !EnchantmentHelper.canStoreEnchantments(stack)) return stack;
        RegistryAccess registryAccess = level.registryAccess();
        Registry<Enchantment> enchantmentRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);
        ResourceKey<Enchantment> vanishingCurseKey = Enchantments.VANISHING_CURSE;
        Optional<Holder.Reference<Enchantment>> optVanishingCurseHolder = enchantmentRegistry.getHolder(vanishingCurseKey);
        if (optVanishingCurseHolder.isPresent()) {
            Holder<Enchantment> vanishingCurseHolder = optVanishingCurseHolder.get();
            if (vanishingCurseHolder.value().canEnchant(stack)) {
                ItemEnchantments existing = stack.getTagEnchantments();
                boolean isCompatible = existing.keySet().stream()
                        .allMatch(enchantmentHolder -> Enchantment.areCompatible(enchantmentHolder, vanishingCurseHolder));
                if (isCompatible) {
                    stack.enchant(vanishingCurseHolder, 1);
                }
            }
        }
        return stack;
    }

    @Override
    public boolean conflictsWith(AbstractEvent other) {
        return other instanceof KeepInventoryEvent;
    }
}