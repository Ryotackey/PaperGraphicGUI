package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

final class GuiButtonHitboxCalculatorTest {
    private static final float EPSILON = 1.0E-6F;

    @Test
    void calculatesNextButtonHitboxFromTextMetrics() {
        GuiButtonHitbox hitbox = GuiButtonHitboxCalculator.calculate(Component.text("  Next  "));

        assertEquals(1.0F, hitbox.width(), EPSILON);
        assertEquals(0.275F, hitbox.height(), EPSILON);
    }

    @Test
    void calculatesBackButtonIndependentlyFromNextButton() {
        GuiButtonHitbox next = GuiButtonHitboxCalculator.calculate(Component.text("  Next  "));
        GuiButtonHitbox back = GuiButtonHitboxCalculator.calculate(Component.text("  Back  "));

        assertEquals(1.025F, back.width(), EPSILON);
        assertTrue(back.width() > next.width());
        assertEquals(next.height(), back.height(), EPSILON);
    }

    @Test
    void expandsForMultipleLines() {
        GuiButtonHitbox singleLine = GuiButtonHitboxCalculator.calculate(Component.text("Button"));
        GuiButtonHitbox multipleLines =
                GuiButtonHitboxCalculator.calculate(Component.text("Button\nButton"));

        assertEquals(singleLine.width(), multipleLines.width(), EPSILON);
        assertTrue(multipleLines.height() > singleLine.height());
    }
}
