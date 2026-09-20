package io.github.ryotackey.papergraphicgui;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

final class DemoGuiScreens {
    private static final GuiVector TITLE_POSITION = new GuiVector(0.0, 0.35, 0.0);
    private static final GuiVector BUTTON_POSITION = new GuiVector(0.0, -0.15, 0.0);

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
                        "second"));
        GuiScreen second = new GuiScreen(
                "second",
                title("Second Screen"),
                TITLE_POSITION,
                new GuiButton(
                        "back",
                        buttonLabel("Back"),
                        BUTTON_POSITION,
                        "main"));

        return new GuiScreenSet(main.id(), List.of(main, second));
    }

    static GuiScreenSet createSizeSampleSet() {
        GuiScreen shortButton = new GuiScreen(
                "short",
                title("Short Button"),
                TITLE_POSITION,
                new GuiButton("short-next", buttonLabel("OK"), BUTTON_POSITION, "medium"));
        GuiScreen mediumButton = new GuiScreen(
                "medium",
                title("Medium Button"),
                TITLE_POSITION,
                new GuiButton("medium-next", buttonLabel("Continue"), BUTTON_POSITION, "long"));
        GuiScreen longButton = new GuiScreen(
                "long",
                title("Long Button"),
                TITLE_POSITION,
                new GuiButton(
                        "long-next",
                        buttonLabel("Open Advanced Settings"),
                        BUTTON_POSITION,
                        "short"));

        return new GuiScreenSet(
                shortButton.id(), List.of(shortButton, mediumButton, longButton));
    }

    static Component gridTitle() {
        return title("2 x 2 Hitbox Test");
    }

    static List<GuiGridButton> createGridButtons() {
        return List.of(
                gridButton("top-left", "Top Left", -0.95, 0.15, NamedTextColor.AQUA),
                gridButton("top-right", "Top Right", 0.95, 0.15, NamedTextColor.GREEN),
                gridButton("bottom-left", "Bottom Left", -0.95, -0.45, NamedTextColor.YELLOW),
                gridButton("bottom-right", "Bottom Right", 0.95, -0.45, NamedTextColor.LIGHT_PURPLE));
    }

    private static GuiGridButton gridButton(
            String id, String label, double x, double y, NamedTextColor messageColor) {
        return new GuiGridButton(
                id,
                buttonLabel(label),
                new GuiVector(x, y, 0.0),
                Component.text("Clicked: " + label, messageColor));
    }

    private static Component title(String text) {
        return Component.text(text, NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
    }

    private static Component buttonLabel(String text) {
        return Component.text("  " + text + "  ", NamedTextColor.WHITE);
    }
}
