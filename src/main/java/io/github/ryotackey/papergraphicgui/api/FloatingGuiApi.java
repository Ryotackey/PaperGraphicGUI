package io.github.ryotackey.papergraphicgui.api;

import io.github.ryotackey.papergraphicgui.screen.GuiScreenSet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface FloatingGuiApi {
    void open(Plugin ownerPlugin, Player player, GuiScreenSet screens);

    boolean close(Player player);

    boolean isOpen(Player player);
}
