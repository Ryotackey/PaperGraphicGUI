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
            String targetScreenId = screen.button().targetScreenId();
            if (!screensById.containsKey(targetScreenId)) {
                throw new IllegalArgumentException(
                        "Unknown target GUI screen ID: " + targetScreenId);
            }
        }

        this.initialScreenId = initialScreenId;
        this.screens = Map.copyOf(screensById);
    }

    GuiScreen initialScreen() {
        return this.screens.get(this.initialScreenId);
    }

    GuiScreen targetScreen(GuiButton button) {
        Objects.requireNonNull(button, "button");
        GuiScreen targetScreen = this.screens.get(button.targetScreenId());
        if (targetScreen == null) {
            throw new IllegalArgumentException(
                    "Unknown target GUI screen ID: " + button.targetScreenId());
        }
        return targetScreen;
    }
}
