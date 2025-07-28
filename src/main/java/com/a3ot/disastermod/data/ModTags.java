package com.a3ot.disastermod.data;

import com.a3ot.disastermod.Disastermod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> IMPORTANT_BLOCKS = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "important_blocks")
        );
        public static final TagKey<Block> BOTANOPHOBIA = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "botanophobia")
        );
    }
}
