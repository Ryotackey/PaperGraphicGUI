package io.github.ryotackey.papergraphicgui;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

final class DemoGuiScreens {
    private static final GuiVector TITLE_POSITION = new GuiVector(0.0, 0.35, 0.0);
    private static final GuiVector BUTTON_POSITION = new GuiVector(0.0, -0.15, 0.0);
    private static final float BUTTON_WIDTH = 1.2F;
    private static final float BUTTON_HEIGHT = 0.45F;

    private DemoGuiScreens() {}

    static GuiScreenFlow createFlow() {
        GuiScreen main = new GuiScreen(
                "main",
                title("Main Screen"),
                TITLE_POSITION,
                new GuiButton("next", buttonLabel("Next"), BUTTON_POSITION, BUTTON_WIDTH, BUTTON_HEIGHT));
        GuiScreen second = new GuiScreen(
                "second",
                title("Second Screen"),
                TITLE_POSITION,
                new GuiButton("back", buttonLabel("Back"), BUTTON_POSITION, BUTTON_WIDTH, BUTTON_HEIGHT));

        return new GuiScreenFlow(
                main.id(),
                List.of(main, second),
                Map.of(
                        new GuiScreenFlow.Transition(main.id(), main.button().id()), second.id(),
                        new GuiScreenFlow.Transition(second.id(), second.button().id()), main.id()));
    }

    private static Component title(String text) {
        return Component.text(text, NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
    }

    private static Component buttonLabel(String text) {
        return Component.text("  " + text + "  ", NamedTextColor.WHITE);
    }
}
