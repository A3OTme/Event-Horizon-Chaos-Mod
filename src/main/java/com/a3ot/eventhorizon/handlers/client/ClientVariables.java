package com.a3ot.eventhorizon.handlers.client;

import com.a3ot.eventhorizon.EventHorizon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.lang.reflect.Field;

@EventBusSubscriber(modid = EventHorizon.MOD_ID, value = Dist.CLIENT)
public class ClientVariables {
    public static boolean
            chibi,
            noJump,
            wideMobs,
            cuteFont,
            invertedControl,
            muted,
            noInventory,
            obfuscate,
            oldWalking,
            pitchMax,
            pitchMin,
            smoothCamera,
            onlySwimming,
            shakyCrosshair,
            noTransparency,
            randomSounds,
            fluidWalker,
            forceNarrator,
            negligentUse,
            flyingFish
                    = false;

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        resetVariables();
    }

    private static void resetVariables() {
        chibi =
        noJump =
        wideMobs =
        cuteFont =
        invertedControl =
        muted =
        noInventory =
        obfuscate =
        oldWalking =
        pitchMax =
        pitchMin =
        smoothCamera =
        onlySwimming =
        shakyCrosshair =
        noTransparency =
        randomSounds =
        fluidWalker =
        forceNarrator =
        negligentUse =
        flyingFish =
        false;
    }

    public static void setVariable(String variableName, boolean value) {
        try {
            Field field = ClientVariables.class.getDeclaredField(variableName);
            if (field.getType() == boolean.class) {
                field.setBoolean(null, value);
            } else {
                EventHorizon.LOGGER.warn("Field '{}' is not of type boolean", variableName);
            }
        } catch (NoSuchFieldException e) {
            EventHorizon.LOGGER.warn("Client variable '{}' not found in ClientVariables", variableName);
        } catch (IllegalAccessException e) {
            EventHorizon.LOGGER.error("Failed to set client variable: {}", variableName, e);
        }
    }
}
