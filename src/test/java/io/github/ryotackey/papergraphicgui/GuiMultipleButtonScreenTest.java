package io.github.ryotackey.papergraphicgui;

import io.github.ryotackey.papergraphicgui.screen.GuiScreen;

import io.github.ryotackey.papergraphicgui.component.GuiAction;
import io.github.ryotackey.papergraphicgui.component.GuiRectangle;
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
        List<GuiRectangle> buttons = buttons(screen);

        assertEquals("grid", screen.id());
        assertEquals(4, buttons.size());
        assertEquals(
                Set.of("top-left", "top-right", "bottom-left", "bottom-right"),
                buttons.stream().map(GuiRectangle::id).collect(Collectors.toSet()));
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
        List<GuiRectangle> buttons = buttons(DemoGuiScreens.createGridSet().initialScreen());

        assertEquals(
                4,
                buttons.stream()
                        .map(button -> assertInstanceOf(
                                        GuiAction.SendMessage.class,
                                        button.action().orElseThrow())
                                .message())
                        .collect(Collectors.toSet())
                        .size());
    }

    @Test
    void rejectsDuplicateButtonIdsWithinOneScreen() {
        GuiRectangle first = button("duplicate");
        GuiRectangle duplicate = button("duplicate");

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreen(
                        "screen",
                        Component.text("Title"),
                        new GuiVector(0.0, 0.0, 0.0),
                        List.of(first, duplicate)));
    }

    private static GuiRectangle button(String id) {
        return new GuiRectangle(
                id,
                new GuiVector(0.0, 0.0, 0.0),
                1.0F,
                0.4F,
                org.bukkit.Material.BLACK_CONCRETE,
                Component.text("Button"),
                new GuiAction.SendMessage(Component.text("Clicked")));
    }

    private static List<GuiRectangle> buttons(GuiScreen screen) {
        return screen.components().stream()
                .filter(GuiRectangle.class::isInstance)
                .map(GuiRectangle.class::cast)
                .filter(GuiRectangle::clickable)
                .toList();
    }
}
