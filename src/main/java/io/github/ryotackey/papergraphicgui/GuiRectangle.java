package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import org.bukkit.Material;

record GuiRectangle(
        String id,
        GuiVector position,
        float width,
        float height,
        Material material) implements GuiComponent {
    GuiRectangle {
        GuiComponent.validate(id, position, width, height);
        Objects.requireNonNull(material, "material");
    }
}
