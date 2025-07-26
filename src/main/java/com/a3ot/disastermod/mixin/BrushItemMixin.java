package com.a3ot.disastermod.mixin;

import com.a3ot.disastermod.data.PlayerBrushData;
import com.a3ot.disastermod.events.server.ArcheologyPlusEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;



@Mixin(BrushItem.class)
public abstract class BrushItemMixin {
    @Unique
    private static final Map<Player, PlayerBrushData> BRUSH_DATA = new WeakHashMap<>();
    @Unique
    private static List<LootTable> disastermod$validLootTables = List.of();

    @Inject(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BrushItem;calculateHitResult(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/phys/HitResult;"))
    private void onBrushUse(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration, CallbackInfo ci) {
        if (!(entity instanceof Player player) || level.isClientSide() || remainingUseDuration < 0 || !ArcheologyPlusEvent.active) return;

        PlayerBrushData data = BRUSH_DATA.computeIfAbsent(player, k -> new PlayerBrushData());

        HitResult hitResult = ((BrushItem) (Object) this).calculateHitResult(player);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos newPos = blockHitResult.getBlockPos();
            BlockState state = level.getBlockState(newPos);

            if (state.is(Blocks.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.SOUL_SAND)) {
                if (data.targetPos == null || !data.targetPos.equals(newPos)) {
                    data.targetPos = newPos;
                    data.lastBrushStartTime = level.getGameTime();
                }
            } else {
                BRUSH_DATA.remove(player);
            }
        }

        if (data.targetPos != null && data.lastBrushStartTime != -1) {
            long elapsed = level.getGameTime() - data.lastBrushStartTime;
            if (elapsed >= 10) {
                disastermod$spawnLootAndDestroyBlock(level, data.targetPos, player, stack);
                BRUSH_DATA.remove(player);
            }
        }
    }

    @Unique
    private void disastermod$spawnLootAndDestroyBlock(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (disastermod$validLootTables.isEmpty()) {
            disastermod$refreshLootTables(serverLevel);
            if (disastermod$validLootTables.isEmpty()) return;
        }

        LootTable lootTable = disastermod$validLootTables.get(level.random.nextInt(disastermod$validLootTables.size()));
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.BLOCK_STATE, blockState)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .create(LootContextParamSets.BLOCK);

        List<ItemStack> loot = lootTable.getRandomItems(lootParams);

        if (!loot.isEmpty()) {
            level.destroyBlock(pos, false);
            ItemStack randomItem = loot.get(level.random.nextInt(loot.size()));

            if (!randomItem.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, randomItem);
                level.addFreshEntity(itemEntity);
            }
        }

        EquipmentSlot equipmentslot = disastermod$getEquipmentSlot(player, stack);
        stack.hurtAndBreak(1, player, equipmentslot);
    }

    @Unique
    private static void disastermod$refreshLootTables(ServerLevel level) {
        disastermod$validLootTables = level.getServer().reloadableRegistries()
                .get().registryOrThrow(Registries.LOOT_TABLE).stream()
                .filter(table -> table != LootTable.EMPTY &&
                        !(table.getLootTableId().getNamespace().equals("minecraft") &&
                                !table.getLootTableId().getPath().startsWith("chests/")))
                .collect(Collectors.toList());
    }

    @Unique
    private EquipmentSlot disastermod$getEquipmentSlot(Player player, ItemStack stack) {
        return stack.equals(player.getItemBySlot(EquipmentSlot.OFFHAND))
                ? EquipmentSlot.OFFHAND
                : EquipmentSlot.MAINHAND;
    }
}
