package io.github.ryotackey.papergraphicgui;

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
        GuiButton next = onlyButton(main);
        GuiScreen second = screens.targetScreen(next);
        GuiButton back = onlyButton(second);
        GuiScreen returnedMain = screens.targetScreen(back);

        assertEquals("main", main.id());
        assertEquals(
                Component.text("Main Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                main.title());
        assertEquals("next", next.id());
        assertEquals(
                "second",
                assertInstanceOf(GuiButtonAction.Navigate.class, next.action()).targetScreenId());
        assertEquals(Component.text("  Next  ", NamedTextColor.WHITE), next.label());
        assertEquals(new GuiVector(0.0, -0.15, 0.0), next.position());
        assertEquals("second", second.id());
        assertEquals(
                Component.text("Second Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                second.title());
        assertEquals("back", back.id());
        assertEquals(
                "main",
                assertInstanceOf(GuiButtonAction.Navigate.class, back.action()).targetScreenId());
        assertSame(main, returnedMain);
    }

    @Test
    void sizeSampleScreensCycleThroughIncreasingButtonWidths() {
        GuiScreenSet screens = DemoGuiScreens.createSizeSampleSet();

        GuiScreen shortButton = screens.initialScreen();
        GuiScreen mediumButton = screens.targetScreen(onlyButton(shortButton));
        GuiScreen longButton = screens.targetScreen(onlyButton(mediumButton));
        GuiScreen returnedShortButton = screens.targetScreen(onlyButton(longButton));

        GuiButtonHitbox shortHitbox =
                GuiButtonHitboxCalculator.calculate(onlyButton(shortButton).label());
        GuiButtonHitbox mediumHitbox =
                GuiButtonHitboxCalculator.calculate(onlyButton(mediumButton).label());
        GuiButtonHitbox longHitbox =
                GuiButtonHitboxCalculator.calculate(onlyButton(longButton).label());

        assertTrue(shortHitbox.width() < mediumHitbox.width());
        assertTrue(mediumHitbox.width() < longHitbox.width());
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
                List.of(new GuiButton(
                        buttonId,
                        Component.text("Button"),
                        new GuiVector(0.0, -0.15, 0.0),
                        new GuiButtonAction.Navigate(targetScreenId))));
    }

    private static GuiButton onlyButton(GuiScreen screen) {
        assertEquals(1, screen.buttons().size());
        return screen.buttons().getFirst();
    }
}
