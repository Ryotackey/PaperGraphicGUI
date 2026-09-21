package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

record GuiRectangleButton(
        String id,
        GuiVector position,
        float width,
        float height,
        Material material,
        Component label,
        GuiButtonAction action) implements GuiComponent {
    GuiRectangleButton {
        GuiComponent.validate(id, position, width, height);
        Objects.requireNonNull(material, "material");
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(action, "action");
    }
}
