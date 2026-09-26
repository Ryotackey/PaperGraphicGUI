package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.ryotackey.papergraphicgui.component.GuiAction;
import io.github.ryotackey.papergraphicgui.component.GuiCallback;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

final class GuiCallbackTest {
    @Test
    void callbackActionExecutesTheProvidedHandler() {
        AtomicBoolean invoked = new AtomicBoolean();
        GuiCallback callback = player -> invoked.set(true);
        GuiAction.Callback action = new GuiAction.Callback(callback);

        action.callback().execute(null);

        assertSame(callback, action.callback());
        assertTrue(invoked.get());
    }

    @Test
    void callbackActionRejectsNullHandler() {
        assertThrows(NullPointerException.class, () -> new GuiAction.Callback(null));
    }
}
