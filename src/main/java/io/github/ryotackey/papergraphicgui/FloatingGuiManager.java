package io.github.ryotackey.papergraphicgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    private final Plugin plugin;
    private final GuiScreenSet screens;
    private final Map<UUID, FloatingGuiSession> sessions = new HashMap<>();

    FloatingGuiManager(Plugin plugin) {
        this.plugin = plugin;
        this.screens = DemoGuiScreens.createSet();
    }

    void open(Player owner) {
        close(owner);

        GuiTransform transform = calculateTransform(owner);
        World world = owner.getWorld();
        Location anchor = toLocation(world, transform.origin());
        List<Entity> spawnedEntities = new ArrayList<>(3);

        try {
            GuiScreen initialScreen = this.screens.initialScreen();
            TextDisplay title = spawnTitle(
                    toLocation(world, transform.toWorld(initialScreen.titlePosition())),
                    initialScreen);
            spawnedEntities.add(title);

            GuiButton initialButton = initialScreen.button();
            Location buttonLocation = toLocation(world, transform.toWorld(initialButton.position()));
            TextDisplay button = spawnButton(buttonLocation, initialScreen);
            spawnedEntities.add(button);

            Interaction interaction = spawnButtonInteraction(
                    interactionLocation(world, transform, initialButton), initialButton);
            spawnedEntities.add(interaction);

            for (Entity entity : spawnedEntities) {
                owner.showEntity(this.plugin, entity);
            }

            this.sessions.put(
                    owner.getUniqueId(),
                    new FloatingGuiSession(
                            owner.getUniqueId(),
                            anchor.clone(),
                            transform,
                            initialScreen,
                            title,
                            button,
                            interaction));
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

        GuiScreen nextScreen = this.screens.targetScreen(session.screen().button());
        renderScreen(session, nextScreen);
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
            display.text(screen.title());
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        });
    }

    private TextDisplay spawnButton(Location location, GuiScreen screen) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(screen.button().label());
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(200, 35, 35, 35));
        });
    }

    private Interaction spawnButtonInteraction(Location location, GuiButton button) {
        return location.getWorld().spawn(location, Interaction.class, interaction -> {
            configureEntity(interaction);
            interaction.setInteractionWidth(button.width());
            interaction.setInteractionHeight(button.height());
            interaction.setResponsive(true);
        });
    }

    private void renderScreen(FloatingGuiSession session, GuiScreen screen) {
        World world = session.anchor().getWorld();
        GuiButton button = screen.button();

        session.title().text(screen.title());
        session.title().teleport(toLocation(world, session.transform().toWorld(screen.titlePosition())));
        session.button().text(button.label());
        session.button().teleport(toLocation(world, session.transform().toWorld(button.position())));
        session.buttonInteraction().setInteractionWidth(button.width());
        session.buttonInteraction().setInteractionHeight(button.height());
        session.buttonInteraction().teleport(interactionLocation(world, session.transform(), button));
    }

    private static Location interactionLocation(
            World world, GuiTransform transform, GuiButton button) {
        GuiVector position = button.position();
        GuiVector interactionPosition =
                new GuiVector(position.x(), position.y() - button.height() / 2.0, position.z());
        return toLocation(world, transform.toWorld(interactionPosition));
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
            GuiTransform transform,
            GuiScreen screen,
            TextDisplay title,
            TextDisplay button,
            Interaction buttonInteraction) {
        FloatingGuiSession withScreen(GuiScreen newScreen) {
            return new FloatingGuiSession(
                    this.ownerUuid,
                    this.anchor,
                    this.transform,
                    newScreen,
                    this.title,
                    this.button,
                    this.buttonInteraction);
        }

        List<Entity> entities() {
            return List.of(this.title, this.button, this.buttonInteraction);
        }
    }
}
