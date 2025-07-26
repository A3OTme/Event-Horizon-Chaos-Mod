package com.a3ot.disastermod.events;

import com.a3ot.disastermod.config.GeneralConfig;
import com.a3ot.disastermod.events.client.PitchMaxEventEvent;
import com.a3ot.disastermod.events.client.PitchMinEventEvent;
import com.a3ot.disastermod.events.server.*;
import com.a3ot.disastermod.handlers.ServerHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface AbstractEvent {

    /**
     * Called at the beginning of any event
     */
    default void onStart(ServerLevel level) {}

    /**
     * Called periodically during an active event
     */
    default void onTick(ServerLevel level) {}

    /**
    * Determines whether this event requires a periodic call to the onTick method.
    */
    default boolean requiresPeriodicTick() {
        return false;
    }

    /**
     * Called at the end of an active event. Most often to disable it.
     */
    default void onEnd(ServerLevel level) {}

    /**
     * Called at the beginning of any client event
     */
    @OnlyIn(Dist.CLIENT)
    default void onClientStart(Level level) {}

    /**
     * Called periodically during an active client event
     */
    @OnlyIn(Dist.CLIENT)
    default void onClientTick(Level level) {}

    /**
     * Determines whether this event requires a periodic call to the onClientTick method.
     */
    default boolean requiresPeriodicClientTick() {
        return false;
    }

    /**
     * Called at the end of an active client event. Most often to disable it.
     */
    @OnlyIn(Dist.CLIENT)
    default void onClientEnd(Level level) {}

    /**
     * Special server logic designed to avoid the imminent death of an innocent player
     */
    default void playerRespawnOrJoin(Player player, Level level) {}

    /**
     * Sets the interval in ticks for calling the onTick() and onClientTick() functions
     */
    default int getInterval() {
        return 20;
    }

    /**
     * The event duration is equal to the product of the timer duration and this multiplier.
     * If the value is set to 0, the event will be instantaneous
     * and will trigger only the OnStart and onClientStart methods.
     */
    default double getDefaultDurationMultiplier() {
        return 2;
    }

    /**
     * The event type is used to classify all events.
     * It is usually used to work with {@link OnlyNegativeEventsEvent} and {@link OnlyPositiveEventsEvent}
     */
    default EventType getType() {
        return EventType.NEGATIVE;
    }

    /**
     * The {@link EventSide} setting helps eliminate unnecessary method executions and network traffic.
     * When configured as SERVER, it prevents Client-side method processing and stops the server from transmitting packets to clients.
     * The CLIENT configuration blocks Server-side method execution, enabling operation within an isolated client environment.
     * BOTH allows processing to occur in both server and client contexts.
     */
    default EventSide getSide() {
        return EventSide.SERVER;
    }

    /**
     * The conflictsWith parameter disables conflicting events by replacing any active ones with the new event.
     * For example, {@link MoonGravityEvent} and {@link NoGravityEvent},
     * or {@link PitchMaxEventEvent} and {@link PitchMinEventEvent}.
     */
    default boolean conflictsWith(AbstractEvent other) {
        return false;
    }

    /**
     * The getSound, getVolume, and getPitch methods
     * determine the sound, volume level, and pitch
     * for the sound played when a new event is triggered.
     */
    default SoundEvent getSound() {
        return SoundEvents.NOTE_BLOCK_PLING.value();
    }

    default float getVolume() {
        return 1.0F;
    }

    default float getPitch() {
        return 1.0F;
    }

    /**
     * Allows for special cases to change the text of the message
     * that is displayed when a new event is triggered.
     * For example, {@link HappyBirthdayEvent}, {@link RandonStructureEvent},
     */
    default Component getMessage(AbstractEvent event) {
        return Component.literal("[Disaster Mod] ").append(Component.translatable(event.getName())
                        .withStyle(event.getColor()))
                .withStyle(style -> style
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable(event.getDescription()))));
    }

    /**
     * Allows you to change the color of the message text
     * that is displayed when a new event is triggered.
     */
    default ChatFormatting getColor() {
        return switch (getType()) {
            case NEGATIVE -> ChatFormatting.RED;
            case POSITIVE -> ChatFormatting.GREEN;
            default -> ChatFormatting.WHITE;
        };
    }

    default void sendEventNotification(ServerLevel level, AbstractEvent event) {
        level.players().forEach(player -> {
            player.sendSystemMessage(event.getMessage(event));
            if (ServerHandler.isClientEventSound(player))
                player.playNotifySound(event.getSound(), SoundSource.MASTER, event.getVolume(), event.getPitch());
        });
    }

    /**
     * Converts the event class name to a string.
     * For example, Event Class name: "RandonTPEvent" to String: "randomTP"
     */
    default String getConfigName() {
        String simpleName = this.getClass().getSimpleName();
        String baseName = simpleName.substring(0, simpleName.length() - 5);
        return Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1);
    }

    //"disastermod.configuration.randomTP"
    default String getName() {
        return "disastermod.configuration." + getConfigName();
    }

    //"disastermod.configuration.randomTP.tooltip"
    default String getDescription() {
        return "disastermod.configuration." + getConfigName() + ".tooltip";
    }

    default boolean getDefaultEnabled() {
        return true;
    }

    default boolean isEnabled() {
        GeneralConfig.EventConfig config = GeneralConfig.EDIT_EVENTS.get(getConfigName());
        return config != null && config.enabled().get();
    }

    default double getDurationMultiplier() {
        GeneralConfig.EventConfig config = GeneralConfig.EDIT_EVENTS.get(getConfigName());
        return config != null && config.duration() != null ? config.duration().get() : 0;
    }
}