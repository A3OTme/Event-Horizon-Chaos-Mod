package com.a3ot.disastermod.data;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Supplier;

//Also look at the enumextensions.json file.
public class ModEnumParams {

    //custom damage sounds
    public static Object getHydrophobiaParams(int idx, Class<?> type) {
        return switch (idx) {
            case 0 -> "disastermod:hydrophobia"; // id
            case 1 -> (Supplier<SoundEvent>) () -> SoundEvents.SNIFFER_HURT; //sound
            default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
        };
    }

    public static Object getNyctophobiaParams(int idx, Class<?> type) {
        return switch (idx) {
            case 0 -> "disastermod:nyctophobia"; // id
            case 1 -> (Supplier<SoundEvent>) () -> SoundEvents.WARDEN_ATTACK_IMPACT; //sound
            default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
        };
    }
}