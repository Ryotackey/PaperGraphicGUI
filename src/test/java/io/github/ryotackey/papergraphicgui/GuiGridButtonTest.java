package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

final class GuiGridButtonTest {
    @Test
    void createsFourButtonsInTwoRowsAndTwoColumns() {
        List<GuiGridButton> buttons = DemoGuiScreens.createGridButtons();

        assertEquals(4, buttons.size());
        assertEquals(
                Set.of("top-left", "top-right", "bottom-left", "bottom-right"),
                buttons.stream().map(GuiGridButton::id).collect(Collectors.toSet()));
        assertTrue(buttons.stream().anyMatch(button -> button.position().x() < 0.0
                && button.position().y() > 0.0));
        assertTrue(buttons.stream().anyMatch(button -> button.position().x() > 0.0
                && button.position().y() > 0.0));
        assertTrue(buttons.stream().anyMatch(button -> button.position().x() < 0.0
                && button.position().y() < 0.0));
        assertTrue(buttons.stream().anyMatch(button -> button.position().x() > 0.0
                && button.position().y() < 0.0));
    }

    @Test
    void givesEachButtonASeparateMessage() {
        List<GuiGridButton> buttons = DemoGuiScreens.createGridButtons();

        assertEquals(4, buttons.stream().map(GuiGridButton::message).collect(Collectors.toSet()).size());
    }
}
