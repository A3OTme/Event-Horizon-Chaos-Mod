package com.a3ot.disastermod.events.utils;

import net.minecraft.network.chat.Style;

public class CuteFragment {
    private final Style style;
    private final String text;

    public CuteFragment(Style style, String string) {
        this.style = style;
        this.text = string;
    }

    public Style getStyle() {
        return style;
    }

    public String getText() {
        return text;
    }
}