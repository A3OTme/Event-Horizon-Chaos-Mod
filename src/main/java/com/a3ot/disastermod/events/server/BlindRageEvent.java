package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.utils.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Original source: <a href="https://github.com/Aizistral-Studios/Enigmatic-Legacy/blob/1.20.X/src/main/java/com/aizistral/enigmaticlegacy/items/CursedRing.java#L319"> Aizistral-Studios/Enigmatic-Legacy (CursedRing.java)</a>
 **/
public class BlindRageEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;

    @Override
    public void setActive() {
        active = true;
    }

    @Override
    public void setInactive() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onStart(ServerLevel level) {
        this.setActive();
    }

    @Override
    public void onEnd(ServerLevel level) {
        level.getServer().getGameRules().getRule(GameRules.RULE_FORGIVE_DEAD_PLAYERS).set(true, level.getServer());
        level.getServer().getGameRules().getRule(GameRules.RULE_UNIVERSAL_ANGER).set(false, level.getServer());
        this.setInactive();
    }

    public static void playerTick(Player player, Level level){
        if(!active) return;
        level.getServer().getGameRules().getRule(GameRules.RULE_FORGIVE_DEAD_PLAYERS).set(false, level.getServer());
        level.getServer().getGameRules().getRule(GameRules.RULE_UNIVERSAL_ANGER).set(true, level.getServer());
        if(!Utils.isValidPlayer(player)) return;
        AABB searchBox = getBoundingBoxAroundEntity(player, 24);
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
                LivingEntity.class,
                searchBox
        );
        for (LivingEntity entity : nearbyEntities) {
            double visibility = player.getVisibilityPercent(entity);
            double angerDistance = Math.max(24 * visibility, 4);

            if (entity.distanceToSqr(player.getX(), player.getY(), player.getZ()) > angerDistance * angerDistance) {
                continue;
            }
            if (entity instanceof Piglin piglin) {
                if (piglin.getTarget() == null || !piglin.getTarget().isAlive()) {
                    if (player.hasLineOfSight(piglin) || player.distanceTo(piglin) <= 4) {
                        PiglinAi.wasHurtBy(piglin, player);
                    } else {
                        continue;
                    }
                }
            }
            if (entity instanceof NeutralMob neutralMob) {
                switch (entity) {
                    case TamableAnimal tamable when tamable.isTame() -> {
                    }
                    case IronGolem golem when golem.isPlayerCreated() -> {
                    }
                    default -> {
                        if (neutralMob.getTarget() == null || !neutralMob.getTarget().isAlive()) {
                            if (player.hasLineOfSight(entity) || player.distanceTo(entity) <= 4) {
                                neutralMob.setTarget(player);
                            }
                        }
                    }
                }
            }
        }
        List<EnderMan> endermen = level.getEntitiesOfClass(EnderMan.class, searchBox);
        for (EnderMan enderman : endermen) {
            if (level.getRandom().nextDouble() <= (0.002)) {
                if (enderman.teleportTowards(player)) {
                    if (player.hasLineOfSight(enderman)) {
                        enderman.setTarget(player);
                    }
                }
            }
        }
    }

    public static AABB getBoundingBoxAroundEntity(Entity entity, final double radius) {
        return new AABB(entity.getX() - radius, entity.getY() - radius, entity.getZ() - radius, entity.getX() + radius, entity.getY() + radius, entity.getZ() + radius);
    }
}