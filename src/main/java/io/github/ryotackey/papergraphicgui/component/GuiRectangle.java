package io.github.ryotackey.papergraphicgui.component;

import io.github.ryotackey.papergraphicgui.GuiVector;
import java.util.Objects;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public record GuiRectangle(
        String id,
        GuiVector position,
        float width,
        float height,
        Material blockMaterial,
        Optional<Component> label,
        Optional<GuiAction> action) implements GuiComponent {
    public GuiRectangle {
        GuiComponent.validate(id, position, width, height);
        Objects.requireNonNull(blockMaterial, "blockMaterial");
        label = Objects.requireNonNull(label, "label");
        action = Objects.requireNonNull(action, "action");
        if (label.isPresent() != action.isPresent()) {
            throw new IllegalArgumentException(
                    "GUI rectangle label and action must either both be present or both be absent");
        }
    }

    public GuiRectangle(
            String id,
            GuiVector position,
            float width,
            float height,
            Material blockMaterial) {
        this(id, position, width, height, blockMaterial, Optional.empty(), Optional.empty());
    }

    public GuiRectangle(
            String id,
            GuiVector position,
            float width,
            float height,
            Material blockMaterial,
            Component label,
            GuiAction action) {
        this(
                id,
                position,
                width,
                height,
                blockMaterial,
                Optional.of(Objects.requireNonNull(label, "label")),
                Optional.of(Objects.requireNonNull(action, "action")));
    }

    public boolean clickable() {
        return this.action.isPresent();
    }
}
