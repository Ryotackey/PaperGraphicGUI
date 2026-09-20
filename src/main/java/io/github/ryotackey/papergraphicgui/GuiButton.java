package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import net.kyori.adventure.text.Component;

record GuiButton(
        String id,
        Component label,
        GuiVector position,
        float width,
        float height,
        String targetScreenId) {
    GuiButton {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI button ID must not be blank");
        }
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(position, "position");
        if (!Float.isFinite(width) || width <= 0.0F) {
            throw new IllegalArgumentException("GUI button width must be finite and positive");
        }
        if (!Float.isFinite(height) || height <= 0.0F) {
            throw new IllegalArgumentException("GUI button height must be finite and positive");
        }
        if (targetScreenId == null || targetScreenId.isBlank()) {
            throw new IllegalArgumentException("GUI button target screen ID must not be blank");
        }
    }
}
