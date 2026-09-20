package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class GuiRaycastTest {
    private static final GuiVector EYE_POSITION = new GuiVector(0.0, 64.125, 0.0);
    private static final GuiVector BUTTON_DISPLAY_ORIGIN = new GuiVector(0.0, 64.0, 2.5);
    private static final GuiVector PLANE_RIGHT = new GuiVector(-1.0, 0.0, 0.0);
    private static final GuiVector PLANE_UP = new GuiVector(0.0, 1.0, 0.0);
    private static final GuiVector PLANE_NORMAL = new GuiVector(0.0, 0.0, 1.0);
    private static final GuiButtonHitbox HITBOX = new GuiButtonHitbox(1.0F, 0.25F);

    @Test
    void acceptsRayThroughTheCenterOfTheBillboardText() {
        assertTrue(GuiRaycast.hitsButton(
                EYE_POSITION,
                new GuiVector(0.0, 0.0, 1.0),
                BUTTON_DISPLAY_ORIGIN,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                HITBOX));
    }

    @Test
    void rejectsRayOutsideTheTextWidth() {
        assertFalse(GuiRaycast.hitsButton(
                EYE_POSITION,
                new GuiVector(0.5, 0.0, 1.0),
                BUTTON_DISPLAY_ORIGIN,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                HITBOX));
    }

    @Test
    void acceptsAnAngledRayAgainstTheFixedPlane() {
        GuiVector viewerPosition = new GuiVector(2.5, 64.125, 0.0);
        assertTrue(GuiRaycast.hitsButton(
                viewerPosition,
                new GuiVector(-1.0, 0.0, 1.0),
                BUTTON_DISPLAY_ORIGIN,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                HITBOX));
    }

    @Test
    void rejectsRayPointingAwayFromTheGui() {
        assertFalse(GuiRaycast.hitsButton(
                EYE_POSITION,
                new GuiVector(0.0, 0.0, -1.0),
                BUTTON_DISPLAY_ORIGIN,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                HITBOX));
    }

    @Test
    void rejectsRayParallelToTheFixedPlane() {
        GuiVector viewerPosition = new GuiVector(0.0, 66.5, 2.5);
        assertFalse(GuiRaycast.hitsButton(
                viewerPosition,
                new GuiVector(0.0, -1.0, 0.0),
                BUTTON_DISPLAY_ORIGIN,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                HITBOX));
    }

    @Test
    void rejectsRayBelowTheDisplayOrigin() {
        assertFalse(GuiRaycast.hitsButton(
                new GuiVector(0.0, 63.99, 0.0),
                new GuiVector(0.0, 0.0, 1.0),
                BUTTON_DISPLAY_ORIGIN,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                HITBOX));
    }
}
