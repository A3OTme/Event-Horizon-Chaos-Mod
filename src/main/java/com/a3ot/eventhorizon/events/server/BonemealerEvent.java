package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.EventHorizon;
import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.utils.IActiveStateEvent;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BonemealerEvent implements AbstractEvent, IActiveStateEvent {
    private static boolean active = false;
    private static final Map<Player, Boolean> wasCrouchingLastTick = new HashMap<>();
    private static final Map<Player, Long> sprintCooldowns = new HashMap<>();
    private static final long SPRINT_INTERVAL = 5;

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
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> {
            player.setSprinting(false);
            player.setDeltaMovement(0, 0 ,0);
            player.resetFallDistance();
            BlockPos playerPos = player.blockPosition();
            int x = playerPos.getX();
            int z = playerPos.getZ();
            int y = playerPos.getY();
            player.teleportTo(x + 0.5, y, z + 0.5);
            BlockPos spawnPos = playerPos.below().west(5).north(5);
            spawnStructureTemplate(level, spawnPos);
        });
    }

    @Override
    public void onEnd(ServerLevel level) {
        this.setInactive();
    }

    public static void playerTick(Player player, Level level) {
        if (!active) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!Utils.isValidPlayer(serverPlayer)) return;
        if (!player.onGround()) return;

        boolean crouching = player.isCrouching();
        boolean wasCrouching = wasCrouchingLastTick.getOrDefault(player, false);

        boolean sprinting = player.isSprinting();

        long currentTime = level.getGameTime();

        if (!wasCrouching && crouching) {
            applyBonemeal(serverPlayer, level);
        }

        if (sprinting && !crouching) {
            Long lastUseTime = sprintCooldowns.get(player);
            if (lastUseTime == null || currentTime - lastUseTime >= SPRINT_INTERVAL) {
                applyBonemeal(serverPlayer, level);
                sprintCooldowns.put(player, currentTime);
            }
        } else {
            sprintCooldowns.remove(player);
        }

        wasCrouchingLastTick.put(player, crouching);
    }

    private static void applyBonemeal(ServerPlayer serverPlayer, Level level) {
        BlockPos pos = serverPlayer.getOnPos();
        if (BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, pos.above(), serverPlayer) ||
                BoneMealItem.growWaterPlant(ItemStack.EMPTY, level, pos.above(), Direction.DOWN)) {
            level.levelEvent(1505, pos.above(), 15);
        } else if (BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, pos, serverPlayer)) {
            level.levelEvent(1505, pos, 15);
        } else if (BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, serverPlayer.blockPosition(), serverPlayer)) {
            level.levelEvent(1505, serverPlayer.blockPosition(), 15);
        }
    }

    public void spawnStructureTemplate(ServerLevel level, BlockPos structurePos) {
        ResourceLocation structureLocation = ResourceLocation.fromNamespaceAndPath(EventHorizon.MOD_ID, "large_farm");
        StructureTemplate template = loadStructureFromJar(level, structureLocation);
        if (template == null) {
            EventHorizon.LOGGER.error("Failed to load structure: {}", structureLocation);
            return;
        }

        StructurePlaceSettings placeSettings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);

        template.placeInWorld(
                level,
                structurePos,
                structurePos,
                placeSettings,
                level.random,
                2
        );
    }

    //this method is very crude, but it is very easy to implement
    private StructureTemplate loadStructureFromJar(ServerLevel level, ResourceLocation structureLocation) {
        String resourcePath = "/data/" + structureLocation.getNamespace() + "/structures/" + structureLocation.getPath() + ".nbt";
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            EventHorizon.LOGGER.error("Resource not found in JAR: {}", resourcePath);
            return null;
        }
        CompoundTag nbt;
        try (inputStream) {
            nbt = NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StructureTemplate template = new StructureTemplate();
        HolderGetter<Block> blockGetter = level.registryAccess().lookupOrThrow(Registries.BLOCK);
        template.load(blockGetter, nbt);
        return template;
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}