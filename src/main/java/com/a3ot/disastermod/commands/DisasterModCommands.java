package com.a3ot.disastermod.commands;

import com.a3ot.disastermod.ServerTick;
import com.a3ot.disastermod.events.AbstractEvent;
import com.a3ot.disastermod.events.EventSide;
import com.a3ot.disastermod.events.EventsRegistry;
import com.a3ot.disastermod.network.packet.ClientEventPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DisasterModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("event")
                .then(Commands.literal("start")
                        .executes(context -> {
                            if (ServerTick.isEnabled()) {
                                ServerTick.start(context.getSource().getServer());
                                ServerLevel level = context.getSource().getLevel();
                                level.players().forEach(player -> player.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(),SoundSource.MASTER, 1, 1));
                                context.getSource().sendSuccess(() -> Component.translatable("disastermod.commands.start"), true);
                            } else {
                                context.getSource().sendFailure(Component.translatable("disastermod.commands.modDisabled"));
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("pause")
                        .executes(context -> {
                            ServerTick.pause(context.getSource().getServer());
                            context.getSource().sendSuccess(() -> Component.translatable("disastermod.commands.pause"), true);
                            return 1;
                        })
                )
                .then(Commands.literal("run")
                        .requires(source -> source.hasPermission(3))
                        .then(Commands.argument("event", StringArgumentType.string())
                                .suggests(DisasterModCommands::suggestEvents)
                                .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                                        .executes(context -> executeRunCommandWithDuration(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "event"),
                                                IntegerArgumentType.getInteger(context, "duration")
                                        ))
                                )
                                .executes(context -> executeRunCommand(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "event")
                                ))
                        )
                )
                .then(Commands.literal("clear")
                        .requires(source -> source.hasPermission(3))
                        .executes(context -> executeClearCommand(context.getSource(), null))
                        .then(Commands.argument("event", StringArgumentType.string())
                                .suggests(DisasterModCommands::activeEvents)
                                .executes(context -> executeClearCommand(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "event")
                                ))
                        )
                )
                .then(Commands.literal("info")
                        .requires(source -> source.hasPermission(3))
                        .then(Commands.argument("event", StringArgumentType.string())
                                .suggests(DisasterModCommands::suggestEvents)
                                .executes(context -> executeInfoCommand(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "event"))))
                )
        );
    }

    private static CompletableFuture<Suggestions> suggestEvents(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        EventsRegistry.getDisasterEvents()
                .forEach(event -> builder.suggest(event.getConfigName()));
        return CompletableFuture.completedFuture(builder.build());
    }

    private static CompletableFuture<Suggestions> activeEvents(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ServerTick.getActiveEvents().keySet().forEach(event -> builder.suggest(event.getConfigName()));
        return CompletableFuture.completedFuture(builder.build());
    }

    private static int executeRunCommand(CommandSourceStack source, String eventName) {
        return getEventByName(source, eventName)
                .map(event -> {
                    MinecraftServer server = source.getServer();
                    ServerTick.addActiveEvent(event, server);
                    if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onStart);
                    if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.START, server);
                    source.sendSuccess(() -> Component.translatable("disastermod.commands.run.success", Component.translatable(event.getName())), false);
                    return 1;
                })
                .orElse(0);
    }

    private static int executeRunCommandWithDuration(CommandSourceStack source, String eventName, int duration) {
        return getEventByName(source, eventName)
                .map(event -> {
                    MinecraftServer server = source.getServer();
                    ServerTick.addActiveEventWithCustomDuration(event, duration, server);
                    if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onStart);
                    if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.START, server);
                    source.sendSuccess(() -> Component.translatable("disastermod.commands.run.success",
                            Component.translatable(event.getName()), duration), false);
                    return 1;
                })
                .orElse(0);
    }

    private static int executeClearCommand(CommandSourceStack source, @Nullable String eventName) {
        MinecraftServer server = source.getServer();
        if (eventName == null || eventName.isEmpty()) {
            Collection<AbstractEvent> activeEvents = new ArrayList<>(ServerTick.getActiveEvents().keySet());
            ServerTick.getActiveEvents().clear();
            activeEvents.forEach(event -> {
                if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onEnd);
                if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.END, server);
            });
            source.sendSuccess(() -> Component.translatable("disastermod.commands.clear.all"), false);
            return ServerTick.getActiveEvents().size();
        }
        return getEventByName(eventName).map(event -> {
            ServerTick.getActiveEvents().remove(event);
            if (event.getSide() != EventSide.CLIENT) server.getAllLevels().forEach(event::onEnd);
            if (event.getSide() != EventSide.SERVER) ClientEventPacket.sendClientEventPacket(event, ClientEventPacket.EventType.END, server);
            source.sendSuccess(() -> Component.translatable("disastermod.commands.clear.success", Component.translatable(event.getName())), false);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.translatable("disastermod.commands.clear.not_found", eventName));
            return 0;
        });
    }

    private static Optional<AbstractEvent> getEventByName(String eventName) {
        return ServerTick.getActiveEvents().keySet().stream()
                .filter(event -> event.getConfigName().equalsIgnoreCase(eventName))
                .findFirst();
    }

    private static int executeInfoCommand(CommandSourceStack source, String eventName) {
        return getEventByName(source, eventName)
                .map(event -> {
                    Component info = Component.empty()
                            .append(Component.translatable("disastermod.commands.info.name")).append(Component.translatable(event.getName()))
                            .append("\n")
                            .append(Component.translatable("disastermod.commands.info.description")).append(Component.translatable(event.getDescription()))
                            .append("\n")
                            .append(Component.translatable("disastermod.commands.info.type", event.getType().toString()))
                            .append("\n")
                            .append(Component.translatable("disastermod.commands.info.enabled",
                                    Component.translatable(event.isEnabled() ?
                                            "disastermod.commands.generic.enabled" :
                                            "disastermod.commands.generic.disabled")))
                            .append("\n")
                            .append(Component.translatable("disastermod.commands.info.durationMultiplier",
                                    String.valueOf(event.getDurationMultiplier())));

                    source.sendSuccess(() -> info, false);
                    return 1;
                })
                .orElse(0);
    }

    private static Optional<AbstractEvent> getEventByName(CommandSourceStack source, String name) {
        AbstractEvent event = EventsRegistry.getDisasterEventByName(name);
        if (event == null) {
            source.sendFailure(Component.translatable("disastermod.commands.eventNotFound", name));
        }
        return Optional.ofNullable(event);
    }
}