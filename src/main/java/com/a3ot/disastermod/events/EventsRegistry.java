package com.a3ot.disastermod.events;

import com.a3ot.disastermod.config.GeneralConfig;
import com.a3ot.disastermod.events.both.*;
import com.a3ot.disastermod.events.client.*;
import com.a3ot.disastermod.events.server.*;

import java.util.*;

public class EventsRegistry {
    private static final List<AbstractEvent> DISASTER_EVENTS = new ArrayList<>();
    private static volatile List<AbstractEvent> ENABLED_EVENTS = List.of();
    private static final Map<String, AbstractEvent> EVENT_MAP = new HashMap<>();
    private static volatile List<AbstractEvent> POSITIVE_EVENTS = List.of();
    private static volatile List<AbstractEvent> NEGATIVE_EVENTS = List.of();

    static {
        DISASTER_EVENTS.add(new MaceDropChallengeEvent());
        DISASTER_EVENTS.add(new HappyBirthdayEvent());
        DISASTER_EVENTS.add(new AdventureModeEvent());
        DISASTER_EVENTS.add(new CuteFontEvent());
        DISASTER_EVENTS.add(new ObfuscateFontEvent());
        DISASTER_EVENTS.add(new X2TimerEvent());
        DISASTER_EVENTS.add(new X5TimerEvent());
        DISASTER_EVENTS.add(new X2EventTimeEvent());
        DISASTER_EVENTS.add(new InventoryShuffleEvent());
        DISASTER_EVENTS.add(new InventoryCrossEvent());
        DISASTER_EVENTS.add(new WideMiningEvent());
        DISASTER_EVENTS.add(new ChibiEvent());
        DISASTER_EVENTS.add(new DementiaEvent());
        DISASTER_EVENTS.add(new TimeStopEvent());
        DISASTER_EVENTS.add(new RandonStructureEvent());
        DISASTER_EVENTS.add(new BotanophobiaEvent());
        DISASTER_EVENTS.add(new FloorIsMagmaEvent());
        DISASTER_EVENTS.add(new InvertedControlEvent());
        DISASTER_EVENTS.add(new NoJumpEvent());
        DISASTER_EVENTS.add(new UhcEvent());
        DISASTER_EVENTS.add(new EnderBloodEvent());
        DISASTER_EVENTS.add(new MoonGravityEvent());
        DISASTER_EVENTS.add(new NoGravityEvent());
        DISASTER_EVENTS.add(new CreativeFlyEvent());
        DISASTER_EVENTS.add(new KeepInventoryEvent());
        DISASTER_EVENTS.add(new PhotosensitizationEvent());
        DISASTER_EVENTS.add(new RandonEntityEvent());
        DISASTER_EVENTS.add(new BabyMobsEvent());
        DISASTER_EVENTS.add(new NoInventoryEvent());
        DISASTER_EVENTS.add(new RottingFoodEvent());
        DISASTER_EVENTS.add(new MutedEvent());
        DISASTER_EVENTS.add(new PitchMaxEventEvent());
        DISASTER_EVENTS.add(new PitchMinEventEvent());
        DISASTER_EVENTS.add(new HydrophobiaEvent());
        DISASTER_EVENTS.add(new ArcheologyPlusEvent());
        DISASTER_EVENTS.add(new OnlyNegativeEventsEvent());
        DISASTER_EVENTS.add(new OnlyPositiveEventsEvent());
        DISASTER_EVENTS.add(new MeteorRainEvent());
        DISASTER_EVENTS.add(new DeadlyFallsEvent());
        DISASTER_EVENTS.add(new RandomEffectEvent());
        DISASTER_EVENTS.add(new RandomMotionEvent());
        DISASTER_EVENTS.add(new ChangeDimensionEvent());
        DISASTER_EVENTS.add(new UnknownEvent());
        DISASTER_EVENTS.add(new SmoothCameraEvent());
        DISASTER_EVENTS.add(new ScopophobiaEvent());
        DISASTER_EVENTS.add(new GillsEvent());
        DISASTER_EVENTS.add(new WhereMobsEvent());
        DISASTER_EVENTS.add(new FluidWalkerEvent());
        DISASTER_EVENTS.add(new OldPlayerAnimationEvent());
        DISASTER_EVENTS.add(new WideMobsEvent());
        DISASTER_EVENTS.add(new AllItemsAreTotemEvent());
        DISASTER_EVENTS.add(new AllItemsAreEdibleEvent());
        DISASTER_EVENTS.add(new NyctophobiaEvent());
        DISASTER_EVENTS.add(new ReducedDurabilityEvent());
        DISASTER_EVENTS.add(new FullBrightnessEvent());
        DISASTER_EVENTS.add(new LowFPSEvent());
        DISASTER_EVENTS.add(new LowRenderDistanceEvent());
        DISASTER_EVENTS.add(new EnchantEquipmentEvent());
        DISASTER_EVENTS.add(new EquipmentUpgradeEvent());
        DISASTER_EVENTS.add(new EquipmentDowngradeEvent());
        DISASTER_EVENTS.add(new EnchantHoneyBottleEvent());
        DISASTER_EVENTS.add(new ReturnToLastDeathEvent());
        DISASTER_EVENTS.add(new CursedVaultEvent());
        DISASTER_EVENTS.add(new OnlySwimmingPoseEvent());
        DISASTER_EVENTS.add(new BlindRageEvent());
        DISASTER_EVENTS.add(new ShakyCrosshairEvent());
        DISASTER_EVENTS.add(new NoTransparencyEvent());

        for (AbstractEvent event : DISASTER_EVENTS) {
            EVENT_MAP.put(event.getConfigName(), event);
        }
    }

    public static AbstractEvent getEvent(String name) {
        return EVENT_MAP.get(name);
    }

    public static List<AbstractEvent> getDisasterEvents() {
        return DISASTER_EVENTS;
    }

    public static List<AbstractEvent> getEnabledEvents() {
        return ENABLED_EVENTS;
    }

    public static List<AbstractEvent> getPositiveEvents() {
        return POSITIVE_EVENTS;
    }

    public static List<AbstractEvent> getNegativeEvents() {
        return NEGATIVE_EVENTS;
    }

    public static AbstractEvent getDisasterEventByName(String name) {
        return EVENT_MAP.get(name);
    }

    public static void updateEnabledEvents(Map<String, GeneralConfig.EventConfig> editEvents) {
        List<AbstractEvent> enabledEvents = new ArrayList<>();
        List<AbstractEvent> positiveEvents = new ArrayList<>();
        List<AbstractEvent> negativeEvents = new ArrayList<>();
        for (AbstractEvent event : DISASTER_EVENTS) {
            GeneralConfig.EventConfig config = editEvents.get(event.getConfigName());
            if (config != null && config.enabled().get()) {
                enabledEvents.add(event);
                if(event.getType()==EventType.NEGATIVE) negativeEvents.add(event);
                else if(event.getType()==EventType.POSITIVE) positiveEvents.add(event);
            }
        }
        ENABLED_EVENTS = List.copyOf(enabledEvents);
        POSITIVE_EVENTS = List.copyOf(positiveEvents);
        NEGATIVE_EVENTS = List.copyOf(negativeEvents);
    }
}
