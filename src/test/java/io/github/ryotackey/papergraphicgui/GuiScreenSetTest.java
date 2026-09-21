package io.github.ryotackey.papergraphicgui;

import io.github.ryotackey.papergraphicgui.component.GuiAction;
import io.github.ryotackey.papergraphicgui.component.GuiRectangle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        GuiRectangle next = onlyButton(main);
        GuiScreen second = screens.targetScreen(next);
        GuiRectangle back = button(second, "back");
        GuiRectangle close = button(second, "close");
        GuiScreen returnedMain = screens.targetScreen(back);

        assertEquals("main", main.id());
        assertEquals(
                Component.text("Main Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                main.title());
        assertEquals("next", next.id());
        assertEquals(
                "second",
                assertInstanceOf(GuiAction.Navigate.class, next.action().orElseThrow())
                        .targetScreenId());
        assertEquals(
                Component.text("  Next  ", NamedTextColor.WHITE),
                next.label().orElseThrow());
        assertEquals(new GuiVector(0.0, -0.15, 0.0), next.position());
        assertEquals("second", second.id());
        assertEquals(
                Component.text("Second Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                second.title());
        assertEquals("back", back.id());
        assertEquals(
                "main",
                assertInstanceOf(GuiAction.Navigate.class, back.action().orElseThrow())
                        .targetScreenId());
        assertInstanceOf(GuiAction.Close.class, close.action().orElseThrow());
        assertSame(main, returnedMain);
    }

    @Test
    void sizeSampleScreensCycleThroughIncreasingButtonWidths() {
        GuiScreenSet screens = DemoGuiScreens.createSizeSampleSet();

        GuiScreen shortButton = screens.initialScreen();
        GuiScreen mediumButton = screens.targetScreen(onlyButton(shortButton));
        GuiScreen longButton = screens.targetScreen(onlyButton(mediumButton));
        GuiScreen returnedShortButton = screens.targetScreen(onlyButton(longButton));

        assertTrue(onlyButton(shortButton).width() < onlyButton(mediumButton).width());
        assertTrue(onlyButton(mediumButton).width() < onlyButton(longButton).width());
        assertSame(shortButton, returnedShortButton);
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
                List.of(new GuiRectangle(
                        buttonId,
                        new GuiVector(0.0, -0.15, 0.0),
                        1.0F,
                        0.4F,
                        org.bukkit.Material.BLACK_CONCRETE,
                        Component.text("Button"),
                        new GuiAction.Navigate(targetScreenId))));
    }

    private static GuiRectangle onlyButton(GuiScreen screen) {
        return screen.components().stream()
                .filter(GuiRectangle.class::isInstance)
                .map(GuiRectangle.class::cast)
                .filter(GuiRectangle::clickable)
                .findFirst()
                .orElseThrow();
    }

    private static GuiRectangle button(GuiScreen screen, String id) {
        return screen.components().stream()
                .filter(GuiRectangle.class::isInstance)
                .map(GuiRectangle.class::cast)
                .filter(rectangle -> rectangle.id().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
