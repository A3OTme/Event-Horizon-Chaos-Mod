package com.a3ot.disastermod.handlers.client;

import com.a3ot.disastermod.Disastermod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.lang.reflect.Field;

@EventBusSubscriber(modid = Disastermod.MODID, value = Dist.CLIENT)
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
            oldPlayerAnimation,
            pitchMax,
            pitchMin,
            smoothCamera,
            onlySwimmingActive,
            shakyCrosshair,
            noTransparency,
            randomSounds,
            fluidWalker,
            forceNarrator
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
        oldPlayerAnimation =
        pitchMax =
        pitchMin =
        smoothCamera =
        onlySwimmingActive =
        shakyCrosshair =
        noTransparency =
        randomSounds =
        fluidWalker =
        forceNarrator =
        false;
    }

    public static void setVariable(String variableName, boolean value) {
        try {
            Field field = ClientVariables.class.getDeclaredField(variableName);
            if (field.getType() == boolean.class) {
                field.setBoolean(null, value);
            } else {
                Disastermod.LOGGER.warn("Field '{}' is not of type boolean", variableName);
            }
        } catch (NoSuchFieldException e) {
            Disastermod.LOGGER.warn("Client variable '{}' not found in ClientVariables", variableName);
        } catch (IllegalAccessException e) {
            Disastermod.LOGGER.error("Failed to set client variable: {}", variableName, e);
        }
    }
}
