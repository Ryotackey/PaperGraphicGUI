package io.github.ryotackey.example;

import io.github.ryotackey.papergraphicgui.GuiVector;
import io.github.ryotackey.papergraphicgui.api.FloatingGuiApi;
import io.github.ryotackey.papergraphicgui.component.GuiAction;
import io.github.ryotackey.papergraphicgui.component.GuiRectangle;
import io.github.ryotackey.papergraphicgui.screen.GuiScreen;
import io.github.ryotackey.papergraphicgui.screen.GuiScreenSet;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperGraphicGuiExamplePlugin extends JavaPlugin {
    private FloatingGuiApi guiApi;

    @Override
    public void onEnable() {
        this.guiApi = Bukkit.getServicesManager().load(FloatingGuiApi.class);
        if (this.guiApi == null) {
            throw new IllegalStateException("PaperGraphicGUI service is unavailable");
        }

        PluginCommand command = this.getCommand("examplegui");
        if (command == null) {
            throw new IllegalStateException("examplegui command is missing from plugin.yml");
        }
        command.setExecutor((sender, ignoredCommand, ignoredLabel, args) -> {
            if (args.length == 1 && args[0].equalsIgnoreCase("disable")) {
                sender.sendMessage("Disabling PaperGraphicGUIExample...");
                Bukkit.getPluginManager().disablePlugin(this);
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by a player.");
                return true;
            }
            this.openExampleGui(player);
            return true;
        });
    }

    private void openExampleGui(Player player) {
        GuiRectangle helloButton = new GuiRectangle(
                "hello",
                new GuiVector(0.0, -0.15, 0.0),
                1.4F,
                0.4F,
                Material.LIGHT_BLUE_CONCRETE,
                Component.text("Click me", NamedTextColor.WHITE),
                new GuiAction.Callback(clickedPlayer -> clickedPlayer.sendMessage(
                        Component.text("Hello from the example plugin!", NamedTextColor.GREEN))),
                Component.text("Runs a custom callback", NamedTextColor.GRAY));

        GuiScreen mainScreen = new GuiScreen(
                "main",
                Component.text("Example Plugin", NamedTextColor.AQUA),
                new GuiVector(0.0, 0.4, 0.0),
                List.of(helloButton));

        GuiScreenSet screens = new GuiScreenSet(mainScreen.id(), List.of(mainScreen));
        this.guiApi.open(this, player, screens);
    }
}
