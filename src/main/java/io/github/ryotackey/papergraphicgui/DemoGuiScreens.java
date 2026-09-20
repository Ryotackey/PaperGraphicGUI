package io.github.ryotackey.papergraphicgui;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

final class DemoGuiScreens {
    private static final GuiVector TITLE_POSITION = new GuiVector(0.0, 0.35, 0.0);
    private static final GuiVector BUTTON_POSITION = new GuiVector(0.0, -0.15, 0.0);
    private static final float BUTTON_WIDTH = 0.8F;
    private static final float BUTTON_HEIGHT = 0.3F;

    private DemoGuiScreens() {}

    static GuiScreenSet createSet() {
        GuiScreen main = new GuiScreen(
                "main",
                title("Main Screen"),
                TITLE_POSITION,
                new GuiButton(
                        "next",
                        buttonLabel("Next"),
                        BUTTON_POSITION,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        "second"));
        GuiScreen second = new GuiScreen(
                "second",
                title("Second Screen"),
                TITLE_POSITION,
                new GuiButton(
                        "back",
                        buttonLabel("Back"),
                        BUTTON_POSITION,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        "main"));

        return new GuiScreenSet(main.id(), List.of(main, second));
    }

    private static Component title(String text) {
        return Component.text(text, NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
    }

    private static Component buttonLabel(String text) {
        return Component.text("  " + text + "  ", NamedTextColor.WHITE);
    }
}
