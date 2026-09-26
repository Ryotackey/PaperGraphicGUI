package io.github.ryotackey.papergraphicgui.component;

import io.github.ryotackey.papergraphicgui.GuiVector;
import java.util.Objects;
import net.kyori.adventure.text.Component;

public record GuiText(
        String id,
        GuiVector position,
        Component text,
        int lineWidth) implements GuiComponent {
    private static final int DEFAULT_LINE_WIDTH = 200;

    public GuiText {
        GuiComponent.validate(id, position);
        Objects.requireNonNull(text, "text");
        if (lineWidth <= 0) {
            throw new IllegalArgumentException("GUI text lineWidth must be positive");
        }
    }

    public GuiText(String id, GuiVector position, Component text) {
        this(id, position, text, DEFAULT_LINE_WIDTH);
    }
}
