package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
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

public class MaceDropChallengeEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            if (!level.isClientSide){
                BlockPos playerPos = player.blockPosition();
                int x = playerPos.getX();
                int z = playerPos.getZ();
                int y = playerPos.getY();

                Utils.airFilling(
                        level,
                        new BlockPos(x - 1, y, z - 1),
                        new BlockPos(x + 1, y + 65, z + 1)
                );

                ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, player.level());
                armorStand.setPos(x + 0.5, y, z + 0.5);
                MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false);
                armorStand.addEffect(glowing);
                player.level().addFreshEntity(armorStand);

                player.teleportTo(x + 0.5, y, z + 0.5);
                MobEffectInstance slow_falling = new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false);
                player.addEffect(slow_falling);

                ItemStack mace = new ItemStack(Items.MACE);
                mace.setDamageValue(mace.getMaxDamage() - 3);
                Utils.changeItemInHand(player, mace);

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
