package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class GuiRaycastTest {
    private static final GuiVector EYE_POSITION = new GuiVector(0.0, 64.0, 0.0);
    private static final GuiVector BUTTON_CENTER = new GuiVector(0.0, 64.0, 2.5);
    private static final GuiVector PLANE_RIGHT = new GuiVector(-1.0, 0.0, 0.0);
    private static final GuiVector PLANE_UP = new GuiVector(0.0, 1.0, 0.0);
    private static final GuiVector PLANE_NORMAL = new GuiVector(0.0, 0.0, 1.0);
    private static final float WIDTH = 1.0F;
    private static final float HEIGHT = 0.25F;

    @Test
    void acceptsRayThroughTheCenterOfTheBillboardText() {
        assertTrue(GuiRaycast.hitsRectangle(
                EYE_POSITION,
                new GuiVector(0.0, 0.0, 1.0),
                BUTTON_CENTER,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                WIDTH,
                HEIGHT));
    }

    @Test
    void rejectsRayOutsideTheTextWidth() {
        assertFalse(GuiRaycast.hitsRectangle(
                EYE_POSITION,
                new GuiVector(0.5, 0.0, 1.0),
                BUTTON_CENTER,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                WIDTH,
                HEIGHT));
    }

    @Test
    void acceptsAnAngledRayAgainstTheFixedPlane() {
        GuiVector viewerPosition = new GuiVector(2.5, 64.0, 0.0);
        assertTrue(GuiRaycast.hitsRectangle(
                viewerPosition,
                new GuiVector(-1.0, 0.0, 1.0),
                BUTTON_CENTER,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                WIDTH,
                HEIGHT));
    }

    @Test
    void rejectsRayPointingAwayFromTheGui() {
        assertFalse(GuiRaycast.hitsRectangle(
                EYE_POSITION,
                new GuiVector(0.0, 0.0, -1.0),
                BUTTON_CENTER,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                WIDTH,
                HEIGHT));
    }

    @Test
    void rejectsRayParallelToTheFixedPlane() {
        GuiVector viewerPosition = new GuiVector(0.0, 66.5, 2.5);
        assertFalse(GuiRaycast.hitsRectangle(
                viewerPosition,
                new GuiVector(0.0, -1.0, 0.0),
                BUTTON_CENTER,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                WIDTH,
                HEIGHT));
    }

    @Test
    void rejectsRayBelowTheDisplayOrigin() {
        assertFalse(GuiRaycast.hitsRectangle(
                new GuiVector(0.0, 63.8, 0.0),
                new GuiVector(0.0, 0.0, 1.0),
                BUTTON_CENTER,
                PLANE_RIGHT,
                PLANE_UP,
                PLANE_NORMAL,
                WIDTH,
                HEIGHT));
    }
}
