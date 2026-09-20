package io.github.ryotackey.papergraphicgui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class GuiScreenFlow {
    private final String initialScreenId;
    private final Map<String, GuiScreen> screens;
    private final Map<Transition, String> transitions;

    GuiScreenFlow(
            String initialScreenId,
            List<GuiScreen> screens,
            Map<Transition, String> transitions) {
        if (initialScreenId == null || initialScreenId.isBlank()) {
            throw new IllegalArgumentException("Initial GUI screen ID must not be blank");
        }
        Objects.requireNonNull(screens, "screens");
        Objects.requireNonNull(transitions, "transitions");

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

        for (Map.Entry<Transition, String> entry : transitions.entrySet()) {
            Transition transition = Objects.requireNonNull(entry.getKey(), "transition");
            String targetScreenId = Objects.requireNonNull(entry.getValue(), "targetScreenId");
            GuiScreen sourceScreen = screensById.get(transition.screenId());
            if (sourceScreen == null) {
                throw new IllegalArgumentException(
                        "Unknown transition source screen ID: " + transition.screenId());
            }
            if (!sourceScreen.button().id().equals(transition.buttonId())) {
                throw new IllegalArgumentException(
                        "Unknown button ID " + transition.buttonId() + " on screen " + transition.screenId());
            }
            if (!screensById.containsKey(targetScreenId)) {
                throw new IllegalArgumentException(
                        "Unknown transition target screen ID: " + targetScreenId);
            }
        }

        this.initialScreenId = initialScreenId;
        this.screens = Map.copyOf(screensById);
        this.transitions = Map.copyOf(transitions);
    }

    GuiScreen initialScreen() {
        return this.screens.get(this.initialScreenId);
    }

    GuiScreen transition(GuiScreen currentScreen, String buttonId) {
        Objects.requireNonNull(currentScreen, "currentScreen");
        Objects.requireNonNull(buttonId, "buttonId");

        String targetScreenId = this.transitions.get(new Transition(currentScreen.id(), buttonId));
        if (targetScreenId == null) {
            throw new IllegalStateException(
                    "No GUI transition for screen " + currentScreen.id() + " and button " + buttonId);
        }
        return this.screens.get(targetScreenId);
    }

    record Transition(String screenId, String buttonId) {
        Transition {
            if (screenId == null || screenId.isBlank()) {
                throw new IllegalArgumentException("Transition screen ID must not be blank");
            }
            if (buttonId == null || buttonId.isBlank()) {
                throw new IllegalArgumentException("Transition button ID must not be blank");
            }
        }
    }
}
