package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class DropChallengeEvent implements AbstractEvent {

    private static final List<ItemStack> ITEM_POOL = List.of(
            new ItemStack(Items.WATER_BUCKET),
            new ItemStack(Items.POWDER_SNOW_BUCKET),
            new ItemStack(Items.WIND_CHARGE),
            new ItemStack(Items.SCAFFOLDING),
            new ItemStack(Items.COBWEB),
            new ItemStack(Blocks.SLIME_BLOCK.asItem()),
            new ItemStack(Blocks.WEEPING_VINES.asItem()),
            new ItemStack(Items.CHORUS_FRUIT),
            new ItemStack(Items.MACE)
    );

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            if (!level.isClientSide) {
                BlockPos playerPos = player.blockPosition();
                int x = playerPos.getX();
                int z = playerPos.getZ();
                int y = playerPos.getY();
                player.teleportTo(x + 0.5, y, z + 0.5);
                Utils.airFilling(
                        level,
                        new BlockPos(x - 1, y, z - 1),
                        new BlockPos(x + 1, y + 65, z + 1)
                );

                ItemStack itemToGive = ITEM_POOL.get(player.getRandom().nextInt(ITEM_POOL.size())).copy();
                Utils.changeItemInHand(player, itemToGive);

                if (itemToGive.is(Items.MACE)) {
                    ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, player.level());
                    armorStand.setPos(x + 0.5, y, z + 0.5);
                    MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false);
                    armorStand.addEffect(glowing);
                    player.level().addFreshEntity(armorStand);
                }
                MobEffectInstance slow_falling = new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false);
                player.addEffect(slow_falling);
                player.hurtMarked = true;
                player.setDeltaMovement(0, 10, 0);
            }
        });
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GRAY;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.MACE_SMASH_AIR;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}