package io.github.ryotackey.papergraphicgui;

record GuiButtonHitbox(float width, float height) {
    GuiButtonHitbox {
        if (!Float.isFinite(width) || width <= 0.0F) {
            throw new IllegalArgumentException("GUI button hitbox width must be finite and positive");
        }
        if (!Float.isFinite(height) || height <= 0.0F) {
            throw new IllegalArgumentException("GUI button hitbox height must be finite and positive");
        }
    }
}
