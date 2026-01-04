package com.a3ot.eventhorizon.events.server;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.events.utils.Utils;
import net.minecraft.BlockUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ChangeDimensionEvent implements AbstractEvent {
    @Override
    public void onStart(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) return;
        List<ServerPlayer> allPlayers = level.getServer().getPlayerList().getPlayers();
        allPlayers.stream().filter(Utils::isValidPlayer).forEach(player -> {
            ServerLevel targetLevel;
            if (player.level().dimension() == Level.END || player.level().dimension() == Level.NETHER) {
                targetLevel = level.getServer().getLevel(Level.OVERWORLD);
            } else {
                targetLevel = level.getServer().getLevel(Level.NETHER);
            }
            if (targetLevel == null) return;

            BlockPos exitPos = calculateExitPosition(player, targetLevel);
            boolean isNether = targetLevel.dimension() == Level.NETHER;
            WorldBorder worldBorder = targetLevel.getWorldBorder();

            Optional<BlockPos> portalPos = targetLevel.getPortalForcer().findClosestPortalPosition(exitPos, isNether, worldBorder);
            DimensionTransition.PostDimensionTransition postTransition = DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET);

            if (portalPos.isPresent()) {
                BlockUtil.FoundRectangle rectangle = BlockUtil.getLargestRectangleAround(
                        portalPos.get(),
                        Direction.Axis.X,
                        21, Direction.Axis.Y, 21,
                        pos -> targetLevel.getBlockState(pos).is(Blocks.NETHER_PORTAL)
                );
                Vec3 safePosition = calculateSafePosition(targetLevel, rectangle, player);
                DimensionTransition transition = new DimensionTransition(
                        targetLevel, safePosition, player.getDeltaMovement(), player.getYRot(), player.getXRot(), false, postTransition
                );
                player.changeDimension(transition);
            } else {
                Direction.Axis axis = Direction.Axis.X;
                BlockPos potentialPos = findPotentialPortalPosition(targetLevel, exitPos, axis);
                if (potentialPos != null) {
                    BlockUtil.FoundRectangle virtualRectangle = new BlockUtil.FoundRectangle(potentialPos, 2, 3);
                    Vec3 safePosition = calculateSafePosition(targetLevel, virtualRectangle, player);
                    DimensionTransition transition = new DimensionTransition(
                            targetLevel, safePosition, player.getDeltaMovement(), player.getYRot(), player.getXRot(), false, postTransition
                    );
                    player.changeDimension(transition);
                } else {
                    BlockPos defaultPos = targetLevel.getSharedSpawnPos();
                    Vec3 safePosition = new Vec3(defaultPos.getX() + 0.5, defaultPos.getY(), defaultPos.getZ() + 0.5);
                    DimensionTransition transition = new DimensionTransition(
                            targetLevel, safePosition, player.getDeltaMovement(), player.getYRot(), player.getXRot(), false, postTransition
                    );
                    player.changeDimension(transition);
                }
            }
        });
    }

    private BlockPos findPotentialPortalPosition(ServerLevel level, BlockPos center, Direction.Axis axis) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        WorldBorder border = level.getWorldBorder();
        int maxHeight = Math.min(level.getMaxBuildHeight(), level.getMinBuildHeight() + level.getLogicalHeight()) - 1;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        List<BlockPos> positions = getBfsPositions(center);

        for (BlockPos pos : positions) {
            int groundHeight = Math.min(maxHeight, level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()));
            mutablePos.set(pos.getX(), groundHeight, pos.getZ());
            if (!border.isWithinBounds(mutablePos) ||
                    !border.isWithinBounds(mutablePos.relative(direction, 1))) {
                continue;
            }
            mutablePos.move(direction.getOpposite(), 1);
            for (int y = groundHeight; y >= level.getMinBuildHeight(); y--) {
                mutablePos.setY(y);

                if (canPortalReplaceBlock(level, mutablePos)) {
                    int baseY = y;
                    while (y > level.getMinBuildHeight() &&
                            canPortalReplaceBlock(level, mutablePos.move(Direction.DOWN))) {
                        y--;
                    }

                    if (y + 4 <= maxHeight) {
                        int height = baseY - y;
                        if (height >= 3 && canHostFrame(level, mutablePos.setY(y), direction)) {
                            return mutablePos.immutable();
                        }
                    }
                }
            }
        }
        int minHeight = Math.max(level.getMinBuildHeight() + 1, 70);
        int maxHeightLimit = maxHeight - 9;

        if (maxHeightLimit < minHeight) return null;

        return border.clampToBounds(new BlockPos(
                center.getX() - direction.getStepX(),
                Mth.clamp(center.getY(), minHeight, maxHeightLimit),
                center.getZ() - direction.getStepZ()
        ));
    }
    private List<BlockPos> getBfsPositions(BlockPos center) {
        List<BlockPos> result = new ArrayList<>();
        boolean[][] visited = new boolean[16 * 2 + 1][16 * 2 + 1];
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(center);
        visited[16][16] = true;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            result.add(current);
            for (Direction dir : new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}) {
                BlockPos next = current.relative(dir);
                int dx = next.getX() - center.getX();
                int dz = next.getZ() - center.getZ();
                if (Math.abs(dx) <= 16 && Math.abs(dz) <= 16 &&
                        !visited[dx + 16][dz + 16]) {
                    visited[dx + 16][dz + 16] = true;
                    queue.add(next);
                }
            }
        }
        return result;
    }

    private boolean canPortalReplaceBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() && state.getFluidState().isEmpty();
    }

    private boolean canHostFrame(Level level, BlockPos originalPos, Direction direction) {
        Direction clockwise = direction.getClockWise();
        BlockPos.MutableBlockPos offsetPos = new BlockPos.MutableBlockPos();
        for (int i = -1; i < 3; i++) {
            for (int j = -1; j < 4; j++) {
                clockwise.getStepX();
                offsetPos.setWithOffset(
                        originalPos,
                        direction.getStepX() * i,
                        j,
                        direction.getStepZ() * i
                );
                if (j < 0 && !level.getBlockState(offsetPos).isSolid()) {
                    return false;
                }
                if (j >= 0 && !canPortalReplaceBlock(level, offsetPos)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Vec3 calculateSafePosition(ServerLevel level, BlockUtil.FoundRectangle rectangle, ServerPlayer player) {
        Direction.Axis axis = Direction.Axis.X;
        Blocks.NETHER_PORTAL.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, axis);

        Vec3 relativeOffset = player.getRelativePortalPosition(axis, rectangle);

        double width = rectangle.axis1Size;
        double height = rectangle.axis2Size;

        double x = rectangle.minCorner.getX() + width * relativeOffset.x();
        double y = rectangle.minCorner.getY() + height * relativeOffset.y();
        double z = rectangle.minCorner.getZ() + 0.5;

        return PortalShape.findCollisionFreePosition(
                new Vec3(x, y, z),
                level,
                player,
                player.getDimensions(player.getPose())
        );
    }

    private BlockPos calculateExitPosition(ServerPlayer player, ServerLevel targetLevel) {
        double scale = DimensionType.getTeleportationScale(player.level().dimensionType(), targetLevel.dimensionType());
        BlockPos overworldPos = player.blockPosition();
        return targetLevel.getWorldBorder().clampToBounds(
                overworldPos.getX() * scale,
                overworldPos.getY(),
                overworldPos.getZ() * scale
        );
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public float getVolume() {
        return 0F;
    }
}
