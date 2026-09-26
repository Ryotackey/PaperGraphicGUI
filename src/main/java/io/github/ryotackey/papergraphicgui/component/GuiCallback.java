package io.github.ryotackey.papergraphicgui.component;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface GuiCallback {
    void execute(Player player);
}
