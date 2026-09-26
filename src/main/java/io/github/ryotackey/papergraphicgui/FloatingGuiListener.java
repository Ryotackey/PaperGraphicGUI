package io.github.ryotackey.papergraphicgui;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

final class FloatingGuiListener implements Listener {
    private final FloatingGuiManager guiManager;

    FloatingGuiListener(FloatingGuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK
                && event.getAction() != Action.LEFT_CLICK_AIR
                && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        this.guiManager.handleClick(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        this.guiManager.handleInteraction(event.getPlayer(), event.getRightClicked());
    }

    @EventHandler
    public void onPlayerArmSwing(PlayerArmSwingEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        this.guiManager.handleClick(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent
                || !event.hasChangedPosition()) {
            return;
        }

        Location lockedDestination =
                this.guiManager.lockedDestination(event.getPlayer(), event.getTo());
        if (lockedDestination != null) {
            event.setFrom(lockedDestination);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (this.guiManager.isOpen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        if (this.guiManager.isOpen(event.getPlayer())) {
            event.setVelocity(new Vector());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.guiManager.close(event.getEntity());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.guiManager.recoverPersistedFreezeState(event.getPlayer())) {
            event.getPlayer().sendMessage(
                    net.kyori.adventure.text.Component.text("Recovered GUI player state."));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.guiManager.close(event.getPlayer());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (this.guiManager.isOpen(event.getPlayer())) {
            this.guiManager.close(event.getPlayer());
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        this.guiManager.closeOwnedBy(event.getPlugin());
    }
}
