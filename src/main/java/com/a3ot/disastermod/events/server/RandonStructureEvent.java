package com.a3ot.disastermod.events.server;

import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventType;
import com.a3ot.disastermod.events.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;
import java.util.Objects;

public class RandonStructureEvent implements AbstractEvent {
    private static String nameStructure;
    private static ServerPlayer player;

    @Override
    public void onStart(ServerLevel level) {
        List<ServerPlayer> players = level.players().stream().filter(Utils::isValidPlayer).toList();
        if (players.isEmpty()) return;
        player = players.get(level.random.nextInt(players.size()));
        if (player == null) return;

        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);

        List<Structure> structures = structureRegistry.stream().toList();
        if (structures.isEmpty()) return;

        Structure randomStructure = structures.get(level.random.nextInt(structures.size()));

        nameStructure = Objects.requireNonNull(structureRegistry.getKey(randomStructure)).toString();

        BlockPos pos = player.blockPosition().relative(Direction.NORTH, 5);

        ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();
        StructureStart structureStart = randomStructure.generate(
                level.registryAccess(),
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                level.getChunkSource().randomState(),
                level.getStructureManager(),
                level.getSeed(),
                new ChunkPos(pos),
                0,
                level,
                (context) -> true
        );

        if (!structureStart.isValid()) return;

        BoundingBox boundingBox = structureStart.getBoundingBox();
        ChunkPos minChunk = new ChunkPos(
                SectionPos.blockToSectionCoord(boundingBox.minX()),
                SectionPos.blockToSectionCoord(boundingBox.minZ())
        );
        ChunkPos maxChunk = new ChunkPos(
                SectionPos.blockToSectionCoord(boundingBox.maxX()),
                SectionPos.blockToSectionCoord(boundingBox.maxZ())
        );

        boolean allLoaded = ChunkPos.rangeClosed(minChunk, maxChunk)
                .allMatch(chunkPos -> level.isLoaded(chunkPos.getWorldPosition()));
        if (!allLoaded) return;

        ChunkPos.rangeClosed(minChunk, maxChunk).forEach(chunkPos -> {
            BoundingBox chunkBox = new BoundingBox(
                    chunkPos.getMinBlockX(), level.getMinBuildHeight(),
                    chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(),
                    level.getMaxBuildHeight(), chunkPos.getMaxBlockZ()
            );
            structureStart.placeInChunk(level, level.structureManager(), chunkGenerator, level.getRandom(), chunkBox, chunkPos);
        });
    }

    @Override
    public EventType getType() {
        return EventType.POSITIVE;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GREEN;
    }

    @Override
    public Component getMessage(AbstractEvent event) {
        if (player != null && nameStructure != null) return
                Component.literal("[Disaster Mod] ")
                .append(Component.translatable(event.getName()).append(Component.literal(": ")).withStyle(event.getColor()))
                .append(Component.literal(nameStructure).withStyle(ChatFormatting.WHITE))
                .withStyle(style -> style
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable(event.getDescription()))));

        return AbstractEvent.super.getMessage(event);
    }


    @Override
    public double getDefaultDurationMultiplier() {
        return 0;
    }
}
