package io.github.ryotackey.papergraphicgui;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;

record GuiScreen(String id, Component title, GuiVector titlePosition, List<GuiButton> buttons) {
    GuiScreen {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI screen ID must not be blank");
        }
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(titlePosition, "titlePosition");
        Objects.requireNonNull(buttons, "buttons");
        buttons = List.copyOf(buttons);

        Set<String> buttonIds = new HashSet<>();
        for (GuiButton button : buttons) {
            if (!buttonIds.add(button.id())) {
                throw new IllegalArgumentException("Duplicate GUI button ID: " + button.id());
            }
        }
    }
}
