package io.github.ryotackey.papergraphicgui;

sealed interface GuiComponent permits GuiIcon, GuiRectangle, GuiRectangleButton {
    String id();

    GuiVector position();

    float width();

    float height();

    static void validate(String id, GuiVector position, float width, float height) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("GUI component ID must not be blank");
        }
        if (position == null) {
            throw new NullPointerException("position");
        }
        if (!Float.isFinite(width) || width <= 0.0F) {
            throw new IllegalArgumentException("GUI component width must be finite and positive");
        }
        if (!Float.isFinite(height) || height <= 0.0F) {
            throw new IllegalArgumentException("GUI component height must be finite and positive");
        }
    }
}
