package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import net.kyori.adventure.text.Component;

sealed interface GuiButtonAction {
    record Navigate(String targetScreenId) implements GuiButtonAction {
        public Navigate {
            if (targetScreenId == null || targetScreenId.isBlank()) {
                throw new IllegalArgumentException("GUI target screen ID must not be blank");
            }
        }
    }

    record SendMessage(Component message) implements GuiButtonAction {
        public SendMessage {
            Objects.requireNonNull(message, "message");
        }
    }
}
