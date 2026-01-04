package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.events.EventType;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

import java.util.Locale;

/**
 * Original source: <a href="https://github.com/MayaqqDev/Cynosure/blob/master/common/src/main/kotlin/dev/mayaqq/cynosure/utils/fun/UwUfy.kt"> MayaqqDev/Cynosure (UwUfy.kt)</a>
 **/
public class CuteFontEvent implements AbstractEvent {

    @Override
    public void onClientStart(Level level) {
        ClientVariables.cuteFont = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.cuteFont = false;
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    @Override
    public double getDefaultDurationMultiplier() {
        return 4;
    }

    @Override
    public EventType getType() {
        return EventType.NEUTRAL;
    }

    @Override
    public float getPitch() {
        return 1.4F;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.LIGHT_PURPLE;
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.CAT_AMBIENT;
    }

    private static final String[] PHRASES = {
            "UwU", "owo", "OwO", "uwu", ">w<",
            "^w^", ":3", "^-^", "^_^", "<3", "^u^"
    };

    public static String cuteString(String input) {
        if (!ClientVariables.cuteFont) return input;
        return globalTransform(localTransform(input));
    }

    public static String localTransform(String input) {
        if (input == null || input.isEmpty() || input.matches("\\d+")) return input;

        int stringLength = input.length();

        // Replace r/l with w, R/L with W
        input = input.replaceAll("[rl]", "w").replaceAll("[RL]", "W");
        // Replace ove/OVE with uv/UV
        input = input.replaceAll("ove", "uv").replaceAll("OVE", "UV");
        // Replace punctuation
        input = input.replaceAll("!", "!!1!").replaceAll("\\?", "???");

        input = input.replaceAll("на", "ня").replaceAll("НА", "НЯ");

        input = input.replaceAll("ы", "и").replaceAll("Ы", "И");

        input = input.replaceAll("р", "л").replaceAll("Р", "Л");

        // Handle special characters
        input = input.replaceAll("(?U)%(\\p{L})", "%$1".toLowerCase(Locale.ROOT))
                .replaceAll("(?U)\\$(\\p{L})", "$$1".toLowerCase(Locale.ROOT));

        // Adding duplicate letters or hyphens
        if (stringLength % 2 == 0) {
            input = input.replaceAll("(?U)(\\p{L})(\\b)", "$1$1$1$1$2");
        } else {
            input = input.replaceAll("(?U)\\b(\\p{L})(\\p{L}*)\\b", "$1-$1$2");
        }

        return input;
    }

    public static String globalTransform(String input) {
        if (input == null || input.isEmpty() || input.matches("\\d+")) return input;

        String result = input.length() % 3 == 0 ? input.toUpperCase() : input;
        return input.length() > 3 ? result + " " + PHRASES[input.length() % PHRASES.length] : result;
    }
}
