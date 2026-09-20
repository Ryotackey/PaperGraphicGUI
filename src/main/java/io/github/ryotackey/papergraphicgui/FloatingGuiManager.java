package io.github.ryotackey.papergraphicgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

final class FloatingGuiManager {
    private static final double GUI_DISTANCE = 2.5;
    private static final double TITLE_OFFSET_Y = 0.35;
    private static final double BUTTON_OFFSET_Y = -0.15;
    private static final float BUTTON_WIDTH = 1.2F;
    private static final float BUTTON_HEIGHT = 0.45F;

    private final Plugin plugin;
    private final Map<UUID, FloatingGuiSession> sessions = new HashMap<>();

    FloatingGuiManager(Plugin plugin) {
        this.plugin = plugin;
    }

    void open(Player owner) {
        close(owner);

        Location anchor = calculateAnchor(owner);
        List<Entity> spawnedEntities = new ArrayList<>(3);

        try {
            TextDisplay title = spawnTitle(anchor.clone().add(0, TITLE_OFFSET_Y, 0));
            spawnedEntities.add(title);

            Location buttonLocation = anchor.clone().add(0, BUTTON_OFFSET_Y, 0);
            TextDisplay button = spawnButton(buttonLocation);
            spawnedEntities.add(button);

            Interaction interaction = spawnButtonInteraction(
                    buttonLocation.clone().subtract(0, BUTTON_HEIGHT / 2.0, 0));
            spawnedEntities.add(interaction);

            for (Entity entity : spawnedEntities) {
                owner.showEntity(this.plugin, entity);
            }

            this.sessions.put(
                    owner.getUniqueId(),
                    new FloatingGuiSession(owner.getUniqueId(), title, button, interaction));
        } catch (RuntimeException exception) {
            spawnedEntities.forEach(Entity::remove);
            throw exception;
        }
    }

    boolean close(Player owner) {
        return close(owner.getUniqueId());
    }

    void closeAll() {
        List.copyOf(this.sessions.keySet()).forEach(this::close);
    }

    boolean handleInteraction(Player player, Entity clickedEntity) {
        if (!(clickedEntity instanceof Interaction)) {
            return false;
        }

        FloatingGuiSession session = this.sessions.get(player.getUniqueId());
        if (session == null
                || !session.ownerUuid().equals(player.getUniqueId())
                || !session.buttonInteraction().getUniqueId().equals(clickedEntity.getUniqueId())) {
            return false;
        }

        player.sendMessage(Component.text("Clicked!"));
        return true;
    }

    private boolean close(UUID ownerUuid) {
        FloatingGuiSession session = this.sessions.remove(ownerUuid);
        if (session == null) {
            return false;
        }

        session.entities().forEach(Entity::remove);
        return true;
    }

    private TextDisplay spawnTitle(Location location) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(Component.text("Floating GUI", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        });
    }

    private TextDisplay spawnButton(Location location) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(Component.text("  Click me  ", NamedTextColor.WHITE));
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(200, 35, 35, 35));
        });
    }

    private Interaction spawnButtonInteraction(Location location) {
        return location.getWorld().spawn(location, Interaction.class, interaction -> {
            configureEntity(interaction);
            interaction.setInteractionWidth(BUTTON_WIDTH);
            interaction.setInteractionHeight(BUTTON_HEIGHT);
            interaction.setResponsive(true);
        });
    }

    private static void configureEntity(Entity entity) {
        entity.setVisibleByDefault(false);
        entity.setPersistent(false);
    }

    private static void configureTextDisplay(TextDisplay display) {
        display.setBillboard(Display.Billboard.CENTER);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setShadowed(true);
        display.setSeeThrough(false);
    }

    private static Location calculateAnchor(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector forward = eyeLocation.getDirection().setY(0);

        if (forward.lengthSquared() < 1.0E-6) {
            double yawRadians = Math.toRadians(eyeLocation.getYaw());
            forward = new Vector(-Math.sin(yawRadians), 0, Math.cos(yawRadians));
        } else {
            forward.normalize();
        }

        return eyeLocation.add(forward.multiply(GUI_DISTANCE));
    }

    private record FloatingGuiSession(
            UUID ownerUuid,
            TextDisplay title,
            TextDisplay button,
            Interaction buttonInteraction) {
        List<Entity> entities() {
            return List.of(this.title, this.button, this.buttonInteraction);
        }
    }
}
