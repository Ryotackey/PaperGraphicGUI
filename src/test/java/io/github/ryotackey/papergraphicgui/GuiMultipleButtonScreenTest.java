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
        List<GuiRectangleButton> buttons = buttons(screen);

        assertEquals("grid", screen.id());
        assertEquals(4, buttons.size());
        assertEquals(
                Set.of("top-left", "top-right", "bottom-left", "bottom-right"),
                buttons.stream().map(GuiRectangleButton::id).collect(Collectors.toSet()));
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
        List<GuiRectangleButton> buttons = buttons(DemoGuiScreens.createGridSet().initialScreen());

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
        GuiRectangleButton first = button("duplicate");
        GuiRectangleButton duplicate = button("duplicate");

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreen(
                        "screen",
                        Component.text("Title"),
                        new GuiVector(0.0, 0.0, 0.0),
                        List.of(first, duplicate)));
    }

    private static GuiRectangleButton button(String id) {
        return new GuiRectangleButton(
                id,
                new GuiVector(0.0, 0.0, 0.0),
                1.0F,
                0.4F,
                org.bukkit.Material.BLACK_CONCRETE,
                Component.text("Button"),
                new GuiButtonAction.SendMessage(Component.text("Clicked")));
    }

    private static List<GuiRectangleButton> buttons(GuiScreen screen) {
        return screen.components().stream()
                .filter(GuiRectangleButton.class::isInstance)
                .map(GuiRectangleButton.class::cast)
                .toList();
    }
}
