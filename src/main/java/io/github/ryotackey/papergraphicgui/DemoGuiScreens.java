package io.github.ryotackey.papergraphicgui;

import io.github.ryotackey.papergraphicgui.component.GuiAction;
import io.github.ryotackey.papergraphicgui.component.GuiIcon;
import io.github.ryotackey.papergraphicgui.component.GuiRectangle;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

final class DemoGuiScreens {
    private static final GuiVector TITLE_POSITION = new GuiVector(0.0, 0.35, 0.0);
    private static final GuiVector BUTTON_POSITION = new GuiVector(0.0, -0.15, 0.0);
    private static final float BUTTON_HEIGHT = 0.38F;

    private DemoGuiScreens() {}

    static GuiScreenSet createSet() {
        GuiScreen main = new GuiScreen(
                "main",
                title("Main Screen"),
                TITLE_POSITION,
                List.of(button(
                        "next",
                        "Next",
                        BUTTON_POSITION,
                        1.15F,
                        new GuiAction.Navigate("second"))));
        GuiScreen second = new GuiScreen(
                "second",
                title("Second Screen"),
                TITLE_POSITION,
                List.of(button(
                        "back",
                        "Back",
                        BUTTON_POSITION,
                        1.15F,
                        new GuiAction.Navigate("main"))));

        return new GuiScreenSet(main.id(), List.of(main, second));
    }

    static GuiScreenSet createSizeSampleSet() {
        GuiScreen shortButton = new GuiScreen(
                "short",
                title("Short Button"),
                TITLE_POSITION,
                List.of(button(
                        "short-next",
                        "OK",
                        BUTTON_POSITION,
                        0.75F,
                        new GuiAction.Navigate("medium"))));
        GuiScreen mediumButton = new GuiScreen(
                "medium",
                title("Medium Button"),
                TITLE_POSITION,
                List.of(button(
                        "medium-next",
                        "Continue",
                        BUTTON_POSITION,
                        1.35F,
                        new GuiAction.Navigate("long"))));
        GuiScreen longButton = new GuiScreen(
                "long",
                title("Long Button"),
                TITLE_POSITION,
                List.of(button(
                        "long-next",
                        "Open Advanced Settings",
                        BUTTON_POSITION,
                        2.65F,
                        new GuiAction.Navigate("short"))));

        return new GuiScreenSet(
                shortButton.id(), List.of(shortButton, mediumButton, longButton));
    }

    static GuiScreenSet createGridSet() {
        GuiScreen grid = new GuiScreen(
                "grid",
                title("2 x 2 Hitbox Test"),
                new GuiVector(0.0, 0.85, 0.0),
                List.of(
                        gridButton("top-left", "Top Left", -0.95, 0.15, NamedTextColor.AQUA),
                        gridButton("top-right", "Top Right", 0.95, 0.15, NamedTextColor.GREEN),
                        gridButton(
                                "bottom-left",
                                "Bottom Left",
                                -0.95,
                                -0.45,
                                NamedTextColor.YELLOW),
                        gridButton(
                                "bottom-right",
                                "Bottom Right",
                                0.95,
                                -0.45,
                                NamedTextColor.LIGHT_PURPLE)));
        return new GuiScreenSet(grid.id(), List.of(grid));
    }

    static GuiScreenSet createComponentSampleSet() {
        GuiScreen components = new GuiScreen(
                "components",
                title("Shape & Icon Components"),
                TITLE_POSITION,
                List.of(
                        new GuiRectangle(
                                "panel",
                                new GuiVector(0.0, -0.05, 0.04),
                                2.5F,
                                1.15F,
                                Material.GRAY_CONCRETE),
                        new GuiIcon(
                                "diamond-icon",
                                new GuiVector(-0.8, -0.15, -0.03),
                                0.45F,
                                0.45F,
                                new ItemStack(Material.DIAMOND)),
                        button(
                                "sample-button",
                                "Click me",
                                new GuiVector(0.3, -0.15, -0.03),
                                1.35F,
                                new GuiAction.SendMessage(Component.text(
                                        "Clicked!", NamedTextColor.GREEN)))));
        return new GuiScreenSet(components.id(), List.of(components));
    }

    private static GuiRectangle gridButton(
            String id, String label, double x, double y, NamedTextColor messageColor) {
        return new GuiRectangle(
                id,
                new GuiVector(x, y, 0.0),
                1.55F,
                BUTTON_HEIGHT,
                Material.BLACK_CONCRETE,
                buttonLabel(label),
                new GuiAction.SendMessage(
                        Component.text("Clicked: " + label, messageColor)));
    }

    private static GuiRectangle button(
            String id,
            String label,
            GuiVector position,
            float width,
            GuiAction action) {
        return new GuiRectangle(
                id,
                position,
                width,
                BUTTON_HEIGHT,
                Material.BLACK_CONCRETE,
                buttonLabel(label),
                action);
    }

    private static Component title(String text) {
        return Component.text(text, NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
    }

    private static Component buttonLabel(String text) {
        return Component.text("  " + text + "  ", NamedTextColor.WHITE);
    }
}
