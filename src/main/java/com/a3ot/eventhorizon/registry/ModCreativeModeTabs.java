package com.a3ot.eventhorizon.registry;

import com.a3ot.eventhorizon.EventHorizon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.a3ot.eventhorizon.registry.ModItems.ENCHANTED_HONEY_BOTTLE;
import static com.a3ot.eventhorizon.registry.ModItems.HIDDEN_ITEM_PLACEHOLDER;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EventHorizon.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("eventhorizon_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.eventhorizon"))
            .icon(() -> HIDDEN_ITEM_PLACEHOLDER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(HIDDEN_ITEM_PLACEHOLDER.get());
                output.accept(ENCHANTED_HONEY_BOTTLE.get());
                output.accept(CursedVault.createForCreativeTab(parameters));
            }).build());
}
