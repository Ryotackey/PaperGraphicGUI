package io.github.ryotackey.papergraphicgui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class GuiCommand implements CommandExecutor {
    private final FloatingGuiManager guiManager;

    GuiCommand(FloatingGuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by a player.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            this.guiManager.open(player);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("close")) {
            if (!this.guiManager.close(player)) {
                player.sendMessage(Component.text("No GUI is open.", NamedTextColor.RED));
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("sizes")) {
            this.guiManager.openSizeSamples(player);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("grid")) {
            this.guiManager.openGridSample(player);
            return true;
        }

        player.sendMessage(Component.text("Usage: /gui [close|sizes|grid]", NamedTextColor.RED));
        return true;
    }
}
