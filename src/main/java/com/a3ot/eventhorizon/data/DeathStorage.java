package com.a3ot.eventhorizon.data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathStorage {
    private static final Map<UUID, DeathData> deathDataMap = new HashMap<>();

    public static void recordPlayerDeath(LivingEntity entity) {
        if (entity instanceof Player player) {
            BlockPos deathPos = player.blockPosition();
            ResourceKey<Level> dimension = player.level().dimension();
            UUID playerUUID = player.getUUID();
            deathDataMap.put(playerUUID, new DeathData(deathPos, dimension));
        }
    }

    public static DeathData getDeathData(UUID playerUUID) {
        return deathDataMap.get(playerUUID);
    }

    public static void clearDeathData(UUID playerUUID) {
        deathDataMap.remove(playerUUID);
    }

    public static Map<UUID, DeathData> getWorldDataDeathMap() {
        return new HashMap<>(deathDataMap);
    }

    public static void setWorldDataDeathMap(Map<UUID, DeathData> data) {
        deathDataMap.clear();
        deathDataMap.putAll(data);
    }

    public static class DeathData {
        public final BlockPos pos;
        public final ResourceKey<Level> dimension;

        public DeathData(BlockPos pos, ResourceKey<Level> dimension) {
            this.pos = pos;
            this.dimension = dimension;
        }
    }
}
