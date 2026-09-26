package io.github.ryotackey.papergraphicgui.component;

import io.github.ryotackey.papergraphicgui.GuiVector;
import java.util.Objects;

public sealed interface GuiComponent permits GuiIcon, GuiRectangle, GuiText {
    String id();

    GuiVector position();

    static void validate(String id, GuiVector position) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI component ID must not be blank");
        }
        Objects.requireNonNull(position, "position");
    }

    static void validate(String id, GuiVector position, float width, float height) {
        validate(id, position);
        if (!Float.isFinite(width) || width <= 0.0F) {
            throw new IllegalArgumentException("GUI component width must be finite and positive");
        }
        if (!Float.isFinite(height) || height <= 0.0F) {
            throw new IllegalArgumentException("GUI component height must be finite and positive");
        }
    }
}
