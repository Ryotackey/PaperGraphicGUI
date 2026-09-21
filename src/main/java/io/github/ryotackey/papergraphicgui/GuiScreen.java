package io.github.ryotackey.papergraphicgui;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;

record GuiScreen(String id, Component title, GuiVector titlePosition, List<GuiComponent> components) {
    GuiScreen {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI screen ID must not be blank");
        }
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(titlePosition, "titlePosition");
        Objects.requireNonNull(components, "components");
        components = List.copyOf(components);

        Set<String> componentIds = new HashSet<>();
        for (GuiComponent component : components) {
            if (!componentIds.add(component.id())) {
                throw new IllegalArgumentException("Duplicate GUI component ID: " + component.id());
            }
        }
    }
}
