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
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;

final class FloatingGuiManager {
    private static final double GUI_DISTANCE = 2.5;
    private static final double MAX_GUI_DISTANCE_SQUARED = 6.0 * 6.0;
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

        GuiTransform transform = calculateTransform(owner);
        World world = owner.getWorld();
        Location anchor = toLocation(world, transform.origin());
        List<Entity> spawnedEntities = new ArrayList<>(3);

        try {
            GuiScreen initialScreen = GuiScreen.MAIN;
            TextDisplay title = spawnTitle(
                    toLocation(world, transform.toWorld(new GuiVector(0.0, TITLE_OFFSET_Y, 0.0))),
                    initialScreen);
            spawnedEntities.add(title);

            Location buttonLocation =
                    toLocation(world, transform.toWorld(new GuiVector(0.0, BUTTON_OFFSET_Y, 0.0)));
            TextDisplay button = spawnButton(buttonLocation, initialScreen);
            spawnedEntities.add(button);

            Interaction interaction = spawnButtonInteraction(toLocation(
                    world,
                    transform.toWorld(new GuiVector(0.0, BUTTON_OFFSET_Y - BUTTON_HEIGHT / 2.0, 0.0))));
            spawnedEntities.add(interaction);

            for (Entity entity : spawnedEntities) {
                owner.showEntity(this.plugin, entity);
            }

            this.sessions.put(
                    owner.getUniqueId(),
                    new FloatingGuiSession(
                            owner.getUniqueId(), anchor.clone(), initialScreen, title, button, interaction));
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

    void handleMovement(Player player, Location destination) {
        FloatingGuiSession session = this.sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        Location anchor = session.anchor();
        if (!anchor.getWorld().equals(destination.getWorld())
                || anchor.distanceSquared(destination) > MAX_GUI_DISTANCE_SQUARED) {
            close(player.getUniqueId());
        }
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

        GuiScreen nextScreen = session.screen().next();
        session.title().text(titleText(nextScreen));
        session.button().text(buttonText(nextScreen));
        this.sessions.put(player.getUniqueId(), session.withScreen(nextScreen));
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

    private TextDisplay spawnTitle(Location location, GuiScreen screen) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(titleText(screen));
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        });
    }

    private TextDisplay spawnButton(Location location, GuiScreen screen) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(buttonText(screen));
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

    private static Component titleText(GuiScreen screen) {
        return Component.text(screen.title(), NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
    }

    private static Component buttonText(GuiScreen screen) {
        return Component.text("  " + screen.buttonLabel() + "  ", NamedTextColor.WHITE);
    }

    private static GuiTransform calculateTransform(Player player) {
        Location eyeLocation = player.getEyeLocation();
        org.bukkit.util.Vector viewDirection = eyeLocation.getDirection();
        return GuiTransform.fromView(
                new GuiVector(eyeLocation.getX(), eyeLocation.getY(), eyeLocation.getZ()),
                new GuiVector(viewDirection.getX(), viewDirection.getY(), viewDirection.getZ()),
                eyeLocation.getYaw(),
                GUI_DISTANCE);
    }

    private static Location toLocation(World world, GuiVector position) {
        return new Location(world, position.x(), position.y(), position.z());
    }

    private record FloatingGuiSession(
            UUID ownerUuid,
            Location anchor,
            GuiScreen screen,
            TextDisplay title,
            TextDisplay button,
            Interaction buttonInteraction) {
        FloatingGuiSession withScreen(GuiScreen newScreen) {
            return new FloatingGuiSession(
                    this.ownerUuid,
                    this.anchor,
                    newScreen,
                    this.title,
                    this.button,
                    this.buttonInteraction);
        }

        List<Entity> entities() {
            return List.of(this.title, this.button, this.buttonInteraction);
        }
    }

    private enum GuiScreen {
        MAIN("Main Screen", "Next"),
        SECOND("Second Screen", "Back");

        private final String title;
        private final String buttonLabel;

        GuiScreen(String title, String buttonLabel) {
            this.title = title;
            this.buttonLabel = buttonLabel;
        }

        String title() {
            return this.title;
        }

        String buttonLabel() {
            return this.buttonLabel;
        }

        GuiScreen next() {
            return this == MAIN ? SECOND : MAIN;
        }
    }
}
