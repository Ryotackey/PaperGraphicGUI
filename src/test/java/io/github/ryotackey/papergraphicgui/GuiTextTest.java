package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.ryotackey.papergraphicgui.component.GuiText;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

final class GuiTextTest {
    @Test
    void definesIndependentTextWithDefaultLineWidth() {
        Component content = Component.text("Description");
        GuiText text = new GuiText("description", new GuiVector(0.5, -0.25, 0.0), content);

        assertEquals(content, text.text());
        assertEquals(200, text.lineWidth());
    }

    @Test
    void rejectsNonPositiveLineWidth() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiText(
                        "description",
                        new GuiVector(0.0, 0.0, 0.0),
                        Component.text("Description"),
                        0));
    }
}
