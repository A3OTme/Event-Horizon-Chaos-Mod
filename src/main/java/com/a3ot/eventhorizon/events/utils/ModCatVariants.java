package com.a3ot.eventhorizon.events.utils;

import com.a3ot.eventhorizon.EventHorizon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CatVariant;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ModCatVariants {
    public static final DeferredRegister<CatVariant> CAT_VARIANTS = DeferredRegister.create(Registries.CAT_VARIANT, EventHorizon.MOD_ID);
    public static final Holder<CatVariant> BLACK_AND_WHITE_CAT = CAT_VARIANTS.register(
            "black_and_white_cat",
            () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/cat/black_and_white_cat.png"))
    );
    public static final Holder<CatVariant> ENDERCHEST_CAT = CAT_VARIANTS.register(
            "enderchest_cat",
            () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/cat/enderchest_cat.png"))
    );
    public static final Holder<CatVariant> ENDER_CAT = CAT_VARIANTS.register(
            "ender_cat",
            () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/cat/ender_cat.png"))
    );
    public static final Holder<CatVariant> FROZEN_CAT = CAT_VARIANTS.register(
            "frozen_cat",
            () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "textures/entity/cat/frozen_cat.png"))
    );

    public static List<Holder<CatVariant>> getAllCatVariants() {
         return new ArrayList<>(CAT_VARIANTS.getEntries());
    }
}
