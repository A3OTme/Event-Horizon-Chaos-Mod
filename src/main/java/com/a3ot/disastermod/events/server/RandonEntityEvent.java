package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RandonEntityEvent implements AbstractEvent {
    private static final int ENTITY_COUNT = 8;
    private static final double SPAWN_RADIUS = 7.0;

    @Override
    public void onStart(ServerLevel level) {
        level.players().stream().filter(Utils::isPlayerValid).forEach(player -> {
            if (!player.isAlive()) return;
            RandomSource random = level.random;
            Vec3 playerPos = player.position();

            Utils.airFilling(
                    level,
                    new BlockPos((int) playerPos.x - ENTITY_COUNT - 3, (int) playerPos.y, (int) playerPos.z - ENTITY_COUNT - 3),
                    new BlockPos((int) playerPos.x + ENTITY_COUNT + 3, (int) playerPos.y + 5, (int) playerPos.z + ENTITY_COUNT + 3)
            );
            for (int i = 0; i < ENTITY_COUNT; i++) {
                try {
                    Registry<EntityType<?>> entityTypeRegistry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
                    List<EntityType<?>> summonableEntities = entityTypeRegistry.stream()
                            .filter(EntityType::canSummon)
                            .toList();

                    if (summonableEntities.isEmpty()) continue;

                    EntityType<?> entityType = summonableEntities.get(random.nextInt(summonableEntities.size()));
                    Holder<EntityType<?>> entityTypeHolder = entityTypeRegistry.wrapAsHolder(entityType);
                    double distance = SPAWN_RADIUS;
                    double angle = Math.toRadians(i * (double) 360 / ENTITY_COUNT);
                    double x = playerPos.x + Math.cos(angle) * distance;
                    double z = playerPos.z + Math.sin(angle) * distance;
                    double y = playerPos.y;

                    Vec3 spawnPos = new Vec3(x, y, z);
                    BlockPos belowPos = BlockPos.containing(spawnPos).below();
                    BlockState belowState = level.getBlockState(belowPos);
                    Entity entity = entityTypeHolder.value().create(level);
                    if (entity != null) {
                        if (belowState.isEmpty()) level.setBlock(belowPos, Blocks.STONE.defaultBlockState(), 3);
                        entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, entity.getYRot(), entity.getXRot());
                        if (level.tryAddFreshEntityWithPassengers(entity)) {
                            if (entity instanceof Mob mob) {
                                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, null);
                            }
                        }
                    }
                } catch (Exception e) {
                    level.getServer().sendSystemMessage(Component.literal("Entity spawn error: " + e.getMessage())
                            .withStyle(ChatFormatting.RED));
                }
            }
        });
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
