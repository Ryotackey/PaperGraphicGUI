package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import net.kyori.adventure.text.Component;

record GuiScreen(String id, Component title, GuiVector titlePosition, GuiButton button) {
    GuiScreen {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI screen ID must not be blank");
        }
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(titlePosition, "titlePosition");
        Objects.requireNonNull(button, "button");
    }
}
