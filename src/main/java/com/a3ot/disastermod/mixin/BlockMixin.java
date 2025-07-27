package com.a3ot.disastermod.mixin;

import com.a3ot.disastermod.events.server.RandomBlockDropEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
    @WrapOperation(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDrops(Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"))
    private static List<ItemStack> wyr$customBlockDrops(BlockState state, LootParams.Builder builder, Operation<List<ItemStack>> original, BlockState blockState, ServerLevel world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack) {
        if (entity instanceof Player) {
            if (RandomBlockDropEvent.isActive()) {
                BlockState randomState = BuiltInRegistries.BLOCK.byId((int)(BuiltInRegistries.BLOCK.size() * world.random.nextFloat())).defaultBlockState();
                return original.call(randomState, builder);
            }
        }
        return original.call(state, builder);
    }
}
