package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RandonEntityEvent implements AbstractEvent {

    @Override
    public void onStart(ServerLevel level) {
        RandomSource random = level.random;
        Registry<EntityType<?>> entityTypeRegistry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        List<EntityType<?>> summonableEntities = entityTypeRegistry.stream()
                .filter(EntityType::canSummon)
                .filter(type -> type != EntityType.ENDER_DRAGON)
                .toList();

        for (Entity entity : level.getAllEntities()) {
            if (entity == null) continue;

            if (!(entity instanceof Player) && !(entity instanceof EnderDragon)) {
                Vec3 originalPos = entity.position();
                BlockPos originalBlockPos = entity.blockPosition();

                EntityType<?> newEntityType = summonableEntities.get(random.nextInt(summonableEntities.size()));
                Holder<EntityType<?>> newEntityTypeHolder = entityTypeRegistry.wrapAsHolder(newEntityType);
                Entity newEntity = newEntityTypeHolder.value().create(level);

                if (newEntity != null) {
                    newEntity.moveTo(originalPos.x, originalPos.y, originalPos.z, entity.getYRot(), entity.getXRot());
                    entity.unRide();
                    entity.discard();

                    if (level.tryAddFreshEntityWithPassengers(newEntity)) {
                        if (newEntity instanceof Mob newMob) {
                            newMob.finalizeSpawn(level, level.getCurrentDifficultyAt(originalBlockPos), MobSpawnType.EVENT, null);
                        }
                    } else {
                        level.getServer().sendSystemMessage(Component.literal("Failed to spawn entity " + newEntityType + " at " + originalPos)
                                .withStyle(ChatFormatting.RED));
                    }
                } else {
                    level.getServer().sendSystemMessage(Component.literal("Failed to create entity " + newEntityType)
                            .withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_AQUA;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}