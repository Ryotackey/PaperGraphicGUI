package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import net.kyori.adventure.text.Component;

record GuiButton(
        String id,
        Component label,
        GuiVector position,
        String targetScreenId) {
    GuiButton {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI button ID must not be blank");
        }
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(position, "position");
        if (targetScreenId == null || targetScreenId.isBlank()) {
            throw new IllegalArgumentException("GUI button target screen ID must not be blank");
        }
    }
}
