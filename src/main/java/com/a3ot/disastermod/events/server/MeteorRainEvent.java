package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;

public class MeteorRainEvent implements AbstractEvent {
    @Override
    public void onTick(ServerLevel level) {
        RandomSource random = level.random;
        for (int i = 0; i < 7; i++) {
            level.players().stream().filter(Utils::isValidPlayer).forEach(serverPlayerEntity -> {
                double speed = random.nextInt(4) + 1;
                double angle = Math.toRadians(45);
                double horizontalSpeed = speed * Math.sin(angle);
                double verticalSpeed = -speed * Math.cos(angle);

                double direction = Math.PI;
                double dx = horizontalSpeed * Math.cos(direction);
                double dz = horizontalSpeed * Math.sin(direction);

                LargeFireball meteor = new LargeFireball(
                        serverPlayerEntity.level(),
                        serverPlayerEntity, //Owner cannot be null, but if specify the player as the owner, then neutral mobs will be aggressive towards the player.
                        new Vec3(dx, verticalSpeed, dz),
                        4
                );
                meteor.setOwner(meteor); // It's dumb, but it works.

                meteor.setPos(
                        serverPlayerEntity.getX() + (random.nextInt(100)),
                        serverPlayerEntity.getY() + 50 + (random.nextInt(10) - 5),
                        serverPlayerEntity.getZ() + (random.nextInt(100) - 50)
                );

                serverPlayerEntity.level().addFreshEntity(meteor);
            });
        }
    }

    @Override
    public boolean requiresPeriodicTick() {
        return true;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 1;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.FIRECHARGE_USE;
    }
}