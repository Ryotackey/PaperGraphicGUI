package io.github.ryotackey.papergraphicgui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

final class FloatingGuiListener implements Listener {
    private final FloatingGuiManager guiManager;

    FloatingGuiListener(FloatingGuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        this.guiManager.handleInteraction(event.getPlayer(), event.getRightClicked());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.guiManager.close(event.getPlayer());
    }
}

