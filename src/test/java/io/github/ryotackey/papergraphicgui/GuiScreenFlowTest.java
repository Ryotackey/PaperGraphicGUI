package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

final class GuiScreenFlowTest {
    @Test
    void reproducesMainAndSecondScreenTransitionsWithoutPaper() {
        GuiScreenFlow flow = DemoGuiScreens.createFlow();

        GuiScreen main = flow.initialScreen();
        GuiScreen second = flow.transition(main, main.button().id());
        GuiScreen returnedMain = flow.transition(second, second.button().id());

        assertEquals("main", main.id());
        assertEquals(
                Component.text("Main Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                main.title());
        assertEquals("next", main.button().id());
        assertEquals(Component.text("  Next  ", NamedTextColor.WHITE), main.button().label());
        assertEquals(new GuiVector(0.0, -0.15, 0.0), main.button().position());
        assertEquals(1.2F, main.button().width());
        assertEquals(0.45F, main.button().height());
        assertEquals("second", second.id());
        assertEquals(
                Component.text("Second Screen", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                second.title());
        assertEquals("back", second.button().id());
        assertSame(main, returnedMain);
    }

    @Test
    void rejectsDuplicateScreenIds() {
        GuiScreen first = screen("same", "first-button");
        GuiScreen duplicate = screen("same", "second-button");

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreenFlow("same", List.of(first, duplicate), Map.of()));
    }

    @Test
    void rejectsTransitionForUnknownButtonId() {
        GuiScreen screen = screen("screen", "known-button");

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreenFlow(
                        screen.id(),
                        List.of(screen),
                        Map.of(new GuiScreenFlow.Transition(screen.id(), "unknown-button"), screen.id())));
    }

    private static GuiScreen screen(String screenId, String buttonId) {
        return new GuiScreen(
                screenId,
                Component.text("Title"),
                new GuiVector(0.0, 0.35, 0.0),
                new GuiButton(
                        buttonId,
                        Component.text("Button"),
                        new GuiVector(0.0, -0.15, 0.0),
                        1.2F,
                        0.45F));
    }
}
