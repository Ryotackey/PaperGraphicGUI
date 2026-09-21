package io.github.ryotackey.papergraphicgui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class GuiScreenSet {
    private final String initialScreenId;
    private final Map<String, GuiScreen> screens;

    GuiScreenSet(String initialScreenId, List<GuiScreen> screens) {
        if (initialScreenId == null || initialScreenId.isBlank()) {
            throw new IllegalArgumentException("Initial GUI screen ID must not be blank");
        }
        Objects.requireNonNull(screens, "screens");

        Map<String, GuiScreen> screensById = new HashMap<>();
        for (GuiScreen screen : screens) {
            GuiScreen duplicate = screensById.putIfAbsent(screen.id(), screen);
            if (duplicate != null) {
                throw new IllegalArgumentException("Duplicate GUI screen ID: " + screen.id());
            }
        }
        if (!screensById.containsKey(initialScreenId)) {
            throw new IllegalArgumentException("Unknown initial GUI screen ID: " + initialScreenId);
        }
        for (GuiScreen screen : screensById.values()) {
            for (GuiComponent component : screen.components()) {
                if (component instanceof GuiRectangleButton button
                        && button.action() instanceof GuiButtonAction.Navigate navigate
                        && !screensById.containsKey(navigate.targetScreenId())) {
                    throw new IllegalArgumentException(
                            "Unknown target GUI screen ID: " + navigate.targetScreenId());
                }
            }
        }

        this.initialScreenId = initialScreenId;
        this.screens = Map.copyOf(screensById);
    }

    GuiScreen initialScreen() {
        return this.screens.get(this.initialScreenId);
    }

    GuiScreen targetScreen(GuiRectangleButton button) {
        Objects.requireNonNull(button, "button");
        if (!(button.action() instanceof GuiButtonAction.Navigate navigate)) {
            throw new IllegalArgumentException("GUI button does not navigate: " + button.id());
        }
        GuiScreen targetScreen = this.screens.get(navigate.targetScreenId());
        if (targetScreen == null) {
            throw new IllegalArgumentException(
                    "Unknown target GUI screen ID: " + navigate.targetScreenId());
        }
        return targetScreen;
    }
}
