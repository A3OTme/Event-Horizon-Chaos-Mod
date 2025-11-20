package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.Disastermod;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.utils.IActiveStateEvent;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BonemealCompostEvent implements AbstractEvent, IActiveStateEvent {
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
        level.players().stream().filter(Utils::isValidPlayer).forEach(player -> spawnFarm(level, player.blockPosition().below()));
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

    public void spawnFarm(ServerLevel level, BlockPos structurePos) {
        StructureTemplateManager templateManager = level.getStructureManager();
        ResourceLocation structureLocation = ResourceLocation.fromNamespaceAndPath(Disastermod.MODID, "large_farm");

        Optional<StructureTemplate> templateOptional = templateManager.get(structureLocation);
        if (templateOptional.isEmpty()) {
            System.out.println("Custom structure template not found: " + structureLocation);
            return;
        }

        StructureTemplate template = templateOptional.get();

        StructurePlaceSettings placeSettings = new StructurePlaceSettings()
                .setRotation(net.minecraft.world.level.block.Rotation.NONE)
                .setMirror(net.minecraft.world.level.block.Mirror.NONE)
                .setIgnoreEntities(false)
                .setBoundingBox(net.minecraft.world.level.levelgen.structure.BoundingBox.infinite());

        template.placeInWorld(
                level,
                structurePos,
                structurePos,
                placeSettings,
                level.random,
                2
        );
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }
}