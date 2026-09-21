package io.github.ryotackey.papergraphicgui;

import io.github.ryotackey.papergraphicgui.component.GuiAction;
import io.github.ryotackey.papergraphicgui.component.GuiRectangle;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

final class GuiComponentTest {
    @Test
    void rejectsDuplicateIdsAcrossDifferentRectangleComponentTypes() {
        GuiRectangle rectangle = new GuiRectangle(
                "duplicate", new GuiVector(0.0, 0.0, 0.0), 1.0F, 1.0F, Material.STONE);
        GuiRectangle button = new GuiRectangle(
                "duplicate",
                new GuiVector(0.0, 0.0, 0.0),
                0.5F,
                0.5F,
                Material.BLACK_CONCRETE,
                Component.text("Button"),
                new GuiAction.SendMessage(Component.text("Clicked")));

        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiScreen(
                        "screen",
                        Component.text("Title"),
                        new GuiVector(0.0, 0.0, 0.0),
                        List.of(rectangle, button)));
    }

    @Test
    void rejectsInvalidDimensions() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GuiRectangle(
                        "rectangle",
                        new GuiVector(0.0, 0.0, 0.0),
                        0.0F,
                        1.0F,
                        Material.STONE));
    }
}
