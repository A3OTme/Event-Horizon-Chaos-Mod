package com.a3ot.disastermod.registry;

import com.a3ot.disastermod.Disastermod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CursedVault {

    /**
     * Creates an ItemStack of the cursed storage with the "Curse of Vanishing" enchantment,
     * using registry settings from CreativeModeTab.ItemDisplayParameters.
     * Designed for use in Creative Tab.
     */
    public static ItemStack createForCreativeTab(CreativeModeTab.ItemDisplayParameters parameters) {
        ItemStack stack = createCursedVaultItemStackBase();
        parameters.holders().lookupOrThrow(Registries.ENCHANTMENT)
                .get(Enchantments.VANISHING_CURSE)
                .ifPresent(vanishingCurseHolder -> addVanishingCurse(stack, vanishingCurseHolder));
        return stack;
    }

    /**
     * Creates an ItemStack of the cursed repository with the "Curse of Vanishing" enchantment,
     * using ServerLevel to access the registries.
     * It is intended for use in game logic (for example, in events).
     */
    public static ItemStack createForGameplay(ServerLevel level) {
        ItemStack stack = createCursedVaultItemStackBase();
        addVanishingCurse(stack, level);
        return stack;
    }

    private static ItemStack createCursedVaultItemStackBase() {
        ItemStack vaultItemStack = new ItemStack(Blocks.VAULT);

        BlockItemStateProperties blockStateProperties = vaultItemStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        vaultItemStack.set(DataComponents.BLOCK_STATE, blockStateProperties.with(VaultBlock.OMINOUS, true));

        VaultConfig config = getVaultConfig();

        VaultConfig.CODEC.encodeStart(NbtOps.INSTANCE, config)
                .result()
                .ifPresent(tag -> {
                    if (tag instanceof CompoundTag configTag) {
                        CompoundTag blockEntityData = new CompoundTag();
                        blockEntityData.put("config", configTag);
                        blockEntityData.putString("id", "disastermod:cursed_vault");

                        CustomData.CODEC.parse(NbtOps.INSTANCE, blockEntityData)
                                .result()
                                .ifPresent(customData ->
                                        vaultItemStack.set(DataComponents.BLOCK_ENTITY_DATA, customData)
                                );
                    }
                });

        vaultItemStack.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable("block.disastermod.cursed_vault")
                        .withStyle(Style.EMPTY.withItalic(false).withColor(net.minecraft.ChatFormatting.YELLOW))
        );

        vaultItemStack.set(DataComponents.LORE, new ItemLore(
                List.of(
                        Component.translatable(
                                        "block.disastermod.cursed_vault.lore",
                                        Component.translatable("block.minecraft.diamond_block").withStyle(net.minecraft.ChatFormatting.AQUA))
                                .withStyle(Style.EMPTY.withItalic(false).withColor(net.minecraft.ChatFormatting.GRAY))
                )
        ));
        return vaultItemStack;
    }

    /**
     * Adds the "Curse of Vanishing" enchantment to the ItemStack using an externally acquired Holder<Enchantment> .
     * A universal version for Creative Tab and other contexts.
     */
    private static void addVanishingCurse(ItemStack stack, Holder<Enchantment> vanishingCurseHolder) {
        ItemEnchantments existingEnchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(existingEnchantments);
        mutableEnchantments.upgrade(vanishingCurseHolder, 1);
        ItemEnchantments newEnchantments = mutableEnchantments.toImmutable();
        stack.set(DataComponents.ENCHANTMENTS, newEnchantments);
    }

    /**
     * Adds the "Curse of Vanishing" enchantment to the ItemStack, using ServerLevel to obtain the Holder<Enchantment>.
     * It is convenient for use in game logic.
     */
    private static void addVanishingCurse(ItemStack stack, ServerLevel level) {
        Registry<Enchantment> registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> vanishingCurseHolder = registry.getHolderOrThrow(Enchantments.VANISHING_CURSE);
        addVanishingCurse(stack, vanishingCurseHolder);
    }

    /**
     * Creates a configuration for VaultConfig.
     * Uses a predefined CURSED_VAULT_LOOT_TABLE_KEY.
     */
    private static @NotNull VaultConfig getVaultConfig() {
        double activationRange = 4.0;
        double deactivationRange = 4.5;
        ItemStack keyItem = new ItemStack(Items.DIAMOND_BLOCK.asItem(), 1);
        Optional<ResourceKey<LootTable>> overrideDisplayLootTable = Optional.empty();
        PlayerDetector playerDetector = PlayerDetector.NO_CREATIVE_PLAYERS;
        PlayerDetector.EntitySelector entitySelector = PlayerDetector.EntitySelector.SELECT_FROM_LEVEL;
        return new VaultConfig(
                CURSED_VAULT_LOOT_TABLE_KEY,
                activationRange,
                deactivationRange,
                keyItem,
                overrideDisplayLootTable,
                playerDetector,
                entitySelector
        );
    }

    public static final ResourceKey<LootTable> CURSED_VAULT_LOOT_TABLE_KEY = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "chests/cursed_vault")
    );
}