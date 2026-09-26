package io.github.ryotackey.papergraphicgui.component;

import java.util.Objects;
import net.kyori.adventure.text.Component;

public sealed interface GuiAction {
    record Close() implements GuiAction {}

    record Navigate(String targetScreenId) implements GuiAction {
        public Navigate {
            if (targetScreenId == null || targetScreenId.isBlank()) {
                throw new IllegalArgumentException("GUI target screen ID must not be blank");
            }
        }
    }

    record SendMessage(Component message) implements GuiAction {
        public SendMessage {
            Objects.requireNonNull(message, "message");
        }
    }
}
