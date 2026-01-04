package com.a3ot.eventhorizon.events;

import com.a3ot.eventhorizon.config.GeneralConfig;
import com.a3ot.eventhorizon.events.both.*;
import com.a3ot.eventhorizon.events.client.*;
import com.a3ot.eventhorizon.events.server.*;

import java.util.*;

public class EventsRegistry {
    private static final List<AbstractEvent> ALL_EVENTS = new ArrayList<>();
    private static volatile List<AbstractEvent> ENABLED_EVENTS = List.of();
    private static final Map<String, AbstractEvent> EVENT_MAP = new HashMap<>();
    private static volatile List<AbstractEvent> POSITIVE_EVENTS = List.of();
    private static volatile List<AbstractEvent> NEGATIVE_EVENTS = List.of();

    static {
        ALL_EVENTS.add(new DropChallengeEvent());
        ALL_EVENTS.add(new HappyBirthdayEvent());
        ALL_EVENTS.add(new AdventureModeEvent());
        ALL_EVENTS.add(new CuteFontEvent());
        ALL_EVENTS.add(new ObfuscateFontEvent());
        ALL_EVENTS.add(new X2TimerEvent());
        ALL_EVENTS.add(new X5TimerEvent());
        ALL_EVENTS.add(new X2EventTimeEvent());
        ALL_EVENTS.add(new InventoryShuffleEvent());
        ALL_EVENTS.add(new InventoryCrossEvent());
        ALL_EVENTS.add(new WideMiningEvent());
        ALL_EVENTS.add(new ChibiEvent());
        ALL_EVENTS.add(new DementiaEvent());
        ALL_EVENTS.add(new TimeStopEvent());
        ALL_EVENTS.add(new RandomStructureEvent());
        ALL_EVENTS.add(new BotanophobiaEvent());
        ALL_EVENTS.add(new FloorIsMagmaEvent());
        ALL_EVENTS.add(new InvertedControlEvent());
        ALL_EVENTS.add(new NoJumpEvent());
        ALL_EVENTS.add(new UhcEvent());
        ALL_EVENTS.add(new EnderBloodEvent());
        ALL_EVENTS.add(new LowGravityEvent());
        ALL_EVENTS.add(new NoGravityEvent());
        ALL_EVENTS.add(new CreativeFlyEvent());
        ALL_EVENTS.add(new KeepInventoryEvent());
        ALL_EVENTS.add(new PhotosensitizationEvent());
        ALL_EVENTS.add(new RandomEntityEvent());
        ALL_EVENTS.add(new BabyMobsEvent());
        ALL_EVENTS.add(new NoInventoryEvent());
        ALL_EVENTS.add(new MutedEvent());
        ALL_EVENTS.add(new PitchMaxEvent());
        ALL_EVENTS.add(new PitchMinEvent());
        ALL_EVENTS.add(new ArcheologyPlusEvent());
        ALL_EVENTS.add(new OnlyNegativeEventsEvent());
        ALL_EVENTS.add(new OnlyPositiveEventsEvent());
        ALL_EVENTS.add(new MeteorRainEvent());
        ALL_EVENTS.add(new DeadlyFallsEvent());
        ALL_EVENTS.add(new ChangeDimensionEvent());
        ALL_EVENTS.add(new UnknownEvent());
        ALL_EVENTS.add(new SmoothCameraEvent());
        ALL_EVENTS.add(new FlyingFishEvent());
        ALL_EVENTS.add(new WhereMobsEvent());
        ALL_EVENTS.add(new FluidWalkerEvent());
        ALL_EVENTS.add(new OldWalkingEvent());
        ALL_EVENTS.add(new WideMobsEvent());
        ALL_EVENTS.add(new AllItemsAreTotemEvent());
        ALL_EVENTS.add(new AllItemsAreEdibleEvent());
        ALL_EVENTS.add(new NyctophobiaEvent());
        ALL_EVENTS.add(new NegligentUseEvent());
        ALL_EVENTS.add(new FullBrightnessEvent());
        ALL_EVENTS.add(new LowFPSEvent());
        ALL_EVENTS.add(new LowRenderDistanceEvent());
        ALL_EVENTS.add(new EnchantEquipmentEvent());
        ALL_EVENTS.add(new EquipmentUpgradeEvent());
        ALL_EVENTS.add(new EquipmentDowngradeEvent());
        ALL_EVENTS.add(new EnchantHoneyBottleEvent());
        ALL_EVENTS.add(new ReturnToLastDeathEvent());
        ALL_EVENTS.add(new CursedVaultEvent());
        ALL_EVENTS.add(new OnlySwimmingPoseEvent());
        ALL_EVENTS.add(new BlindRageEvent());
        ALL_EVENTS.add(new ShakyCrosshairEvent());
        ALL_EVENTS.add(new NoTransparencyEvent());
        ALL_EVENTS.add(new RandomBlockDropEvent());
        ALL_EVENTS.add(new RandomSoundsEvent());
        ALL_EVENTS.add(new BonemealerEvent());
        ALL_EVENTS.add(new PetCarrierEvent());
        ALL_EVENTS.add(new ForceNarratorEvent());
        ALL_EVENTS.add(new DamageBringsToExplosionEvent());
        ALL_EVENTS.add(new DeleteChunkEvent());
        ALL_EVENTS.add(new SpicedFoodEvent());
        ALL_EVENTS.add(new HuntingSeasonEvent());
        ALL_EVENTS.add(new SmallGUIEvent());

        for (AbstractEvent event : ALL_EVENTS) {
            EVENT_MAP.put(event.getConfigName(), event);
        }
    }

    public static AbstractEvent getEvent(String name) {
        return EVENT_MAP.get(name);
    }

    public static List<AbstractEvent> getAllEvents() {
        return ALL_EVENTS;
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
        for (AbstractEvent event : ALL_EVENTS) {
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
