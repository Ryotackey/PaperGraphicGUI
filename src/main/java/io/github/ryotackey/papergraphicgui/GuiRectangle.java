package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

record GuiRectangle(
        String id,
        GuiVector position,
        float width,
        float height,
        Material material,
        Optional<Component> label,
        Optional<GuiButtonAction> action) implements GuiComponent {
    GuiRectangle {
        GuiComponent.validate(id, position, width, height);
        Objects.requireNonNull(material, "material");
        label = Objects.requireNonNull(label, "label");
        action = Objects.requireNonNull(action, "action");
        if (label.isPresent() != action.isPresent()) {
            throw new IllegalArgumentException(
                    "GUI rectangle label and action must either both be present or both be absent");
        }
    }

    GuiRectangle(
            String id,
            GuiVector position,
            float width,
            float height,
            Material material) {
        this(id, position, width, height, material, Optional.empty(), Optional.empty());
    }

    GuiRectangle(
            String id,
            GuiVector position,
            float width,
            float height,
            Material material,
            Component label,
            GuiButtonAction action) {
        this(
                id,
                position,
                width,
                height,
                material,
                Optional.of(Objects.requireNonNull(label, "label")),
                Optional.of(Objects.requireNonNull(action, "action")));
    }

    boolean clickable() {
        return this.action.isPresent();
    }
}
