package com.a3ot.disastermod.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record HiddenItemData(ItemStack itemStack) {
    public HiddenItemData(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
    }

    public ItemStack itemStack() {
        return itemStack.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HiddenItemData that = (HiddenItemData) o;
        return ItemStack.isSameItemSameComponents(this.itemStack, that.itemStack);
    }

    @Override
    public int hashCode() {
        return itemStack.isEmpty() ? 0 : itemStack.getDescriptionId().hashCode() + itemStack.getCount();
    }

    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    public static final class Codec {
        public static final com.mojang.serialization.Codec<HiddenItemData> CODEC = ItemStack.OPTIONAL_CODEC
                .xmap(HiddenItemData::new, HiddenItemData::itemStack);
    }

    public static final class Stream {
        public static final StreamCodec<RegistryFriendlyByteBuf, HiddenItemData> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull HiddenItemData decode(@NotNull RegistryFriendlyByteBuf buffer) {
                ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
                return new HiddenItemData(stack);
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, HiddenItemData data) {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, data.isEmpty() ? ItemStack.EMPTY : data.itemStack());
            }
        };
    }
}
