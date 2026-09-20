package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

final class GuiScreenSetTest {
    @Test
    void buttonsOwnMainAndSecondScreenTransitionsWithoutPaper() {
        GuiScreenSet screens = DemoGuiScreens.createSet();

        GuiScreen main = screens.initialScreen();
        GuiScreen second = screens.targetScreen(main.button());
        GuiScreen returnedMain = screens.targetScreen(second.button());

        assertEquals("main", main.id());
        assertEquals(
                Component.text("Main Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                main.title());
        assertEquals("next", main.button().id());
        assertEquals("second", main.button().targetScreenId());
        assertEquals(Component.text("  Next  ", NamedTextColor.WHITE), main.button().label());
        assertEquals(new GuiVector(0.0, -0.15, 0.0), main.button().position());
        assertEquals("second", second.id());
        assertEquals(
                Component.text("Second Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                second.title());
        assertEquals("back", second.button().id());
        assertEquals("main", second.button().targetScreenId());
        assertSame(main, returnedMain);
    }

    @Test
    void rejectsDuplicateScreenIds() {
        GuiScreen first = screen("same", "first-button", "same");
        GuiScreen duplicate = screen("same", "second-button", "same");

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreenSet("same", List.of(first, duplicate)));
    }

    @Test
    void rejectsUnknownTargetScreenId() {
        GuiScreen screen = screen("screen", "button", "unknown");

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreenSet(screen.id(), List.of(screen)));
    }

    private static GuiScreen screen(String screenId, String buttonId, String targetScreenId) {
        return new GuiScreen(
                screenId,
                Component.text("Title"),
                new GuiVector(0.0, 0.35, 0.0),
                new GuiButton(
                        buttonId,
                        Component.text("Button"),
                        new GuiVector(0.0, -0.15, 0.0),
                        targetScreenId));
    }
}
