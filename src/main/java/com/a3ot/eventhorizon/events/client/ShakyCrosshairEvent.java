package com.a3ot.eventhorizon.events.client;

import com.a3ot.eventhorizon.events.AbstractEvent;
import com.a3ot.eventhorizon.events.EventSide;
import com.a3ot.eventhorizon.handlers.client.ClientVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ShakyCrosshairEvent implements AbstractEvent { //todo player ray movement
    private static final ShakyCrosshairEvent INSTANCE = new ShakyCrosshairEvent();

    @Override
    public void onClientStart(Level level) {
        ClientVariables.shakyCrosshair = true;
    }

    @Override
    public void onClientEnd(Level level) {
        ClientVariables.shakyCrosshair = false;
        resetOffset();
    }

    @Override
    public EventSide getSide() {
        return EventSide.CLIENT;
    }

    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    private boolean offsetAppliedThisFrame = false;

    private static final float OFFSET_CHANGE_SPEED = 1f;

    private static final float MAX_OFFSET_PIXELS = 100.0f;

    public static ShakyCrosshairEvent getInstance() {
        return INSTANCE;
    }

    public void updateOffset() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            RandomSource random = mc.level.random;

            float deltaX = (random.nextFloat() * 2.0f - 1.0f) * OFFSET_CHANGE_SPEED;
            float deltaY = (random.nextFloat() * 2.0f - 1.0f) * OFFSET_CHANGE_SPEED;

            this.offsetX += deltaX;
            this.offsetY += deltaY;

            this.offsetX = Math.max(-MAX_OFFSET_PIXELS, Math.min(MAX_OFFSET_PIXELS, this.offsetX));
            this.offsetY = Math.max(-MAX_OFFSET_PIXELS, Math.min(MAX_OFFSET_PIXELS, this.offsetY));

            this.offsetAppliedThisFrame = true;
        }
    }

    public void resetOffset() {
        if (this.offsetX != 0.0f || this.offsetY != 0.0f) {
            this.offsetX = 0.0f;
            this.offsetY = 0.0f;
            this.offsetAppliedThisFrame = true;
        }
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public boolean isOffsetAppliedThisFrame() {
        return offsetAppliedThisFrame;
    }

    public void onFrameEnd() {
        this.offsetAppliedThisFrame = false;
    }
}