package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import net.kyori.adventure.text.Component;

record GuiGridButton(String id, Component label, GuiVector position, Component message) {
    GuiGridButton {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI grid button ID must not be blank");
        }
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(message, "message");
    }
}
