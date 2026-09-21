package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

final class GuiMultipleButtonScreenTest {
    @Test
    void definesGridAsARegularScreenWithFourButtons() {
        GuiScreen screen = DemoGuiScreens.createGridSet().initialScreen();
        List<GuiButton> buttons = screen.buttons();

        assertEquals("grid", screen.id());
        assertEquals(4, buttons.size());
        assertEquals(
                Set.of("top-left", "top-right", "bottom-left", "bottom-right"),
                buttons.stream().map(GuiButton::id).collect(Collectors.toSet()));
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
    void givesEachGridButtonASeparateMessageAction() {
        List<GuiButton> buttons = DemoGuiScreens.createGridSet().initialScreen().buttons();

        assertEquals(
                4,
                buttons.stream()
                        .map(button -> assertInstanceOf(
                                        GuiButtonAction.SendMessage.class, button.action())
                                .message())
                        .collect(Collectors.toSet())
                        .size());
    }

    @Test
    void rejectsDuplicateButtonIdsWithinOneScreen() {
        GuiButton first = button("duplicate");
        GuiButton duplicate = button("duplicate");

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreen(
                        "screen",
                        Component.text("Title"),
                        new GuiVector(0.0, 0.0, 0.0),
                        List.of(first, duplicate)));
    }

    private static GuiButton button(String id) {
        return new GuiButton(
                id,
                Component.text("Button"),
                new GuiVector(0.0, 0.0, 0.0),
                new GuiButtonAction.SendMessage(Component.text("Clicked")));
    }
}
