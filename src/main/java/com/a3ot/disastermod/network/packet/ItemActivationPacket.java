package com.a3ot.disastermod.network.packet;

import com.a3ot.disastermod.Disastermod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ItemActivationPacket(ItemStack itemStack, int entityId) implements CustomPacketPayload {
    public static final Type<ItemActivationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "item_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemActivationPacket> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            ItemActivationPacket::itemStack,
            ByteBufCodecs.INT,
            ItemActivationPacket::entityId,
            ItemActivationPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}