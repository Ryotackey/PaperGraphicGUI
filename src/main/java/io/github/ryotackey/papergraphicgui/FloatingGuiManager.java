package io.github.ryotackey.papergraphicgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;

final class FloatingGuiManager {
    private static final double GUI_DISTANCE = 2.5;
    private static final double MAX_GUI_DISTANCE_SQUARED = 6.0 * 6.0;

    private final Plugin plugin;
    private final GuiScreenSet defaultScreens;
    private final GuiScreenSet sizeSampleScreens;
    private final Map<UUID, FloatingGuiSession> sessions = new HashMap<>();
    private final Map<UUID, GridGuiSession> gridSessions = new HashMap<>();

    FloatingGuiManager(Plugin plugin) {
        this.plugin = plugin;
        this.defaultScreens = DemoGuiScreens.createSet();
        this.sizeSampleScreens = DemoGuiScreens.createSizeSampleSet();
    }

    void open(Player owner) {
        open(owner, this.defaultScreens);
    }

    void openSizeSamples(Player owner) {
        open(owner, this.sizeSampleScreens);
    }

    void openGridSample(Player owner) {
        close(owner);

        GuiTransform transform = calculateTransform(owner);
        World world = owner.getWorld();
        Location anchor = toLocation(world, transform.origin());
        List<Entity> spawnedEntities = new ArrayList<>(5);
        List<GridButtonDisplay> buttons = new ArrayList<>(4);

        try {
            TextDisplay title = spawnTitle(
                    toLocation(world, transform.toWorld(new GuiVector(0.0, 0.85, 0.0))),
                    DemoGuiScreens.gridTitle());
            spawnedEntities.add(title);

            for (GuiGridButton definition : DemoGuiScreens.createGridButtons()) {
                TextDisplay display = spawnButton(
                        toLocation(world, transform.toWorld(definition.position())),
                        definition.label());
                spawnedEntities.add(display);
                buttons.add(new GridButtonDisplay(definition, display));
            }

            for (Entity entity : spawnedEntities) {
                owner.showEntity(this.plugin, entity);
            }

            this.gridSessions.put(
                    owner.getUniqueId(),
                    new GridGuiSession(owner.getUniqueId(), anchor.clone(), transform, title, buttons));
        } catch (RuntimeException exception) {
            spawnedEntities.forEach(Entity::remove);
            throw exception;
        }
    }

    private void open(Player owner, GuiScreenSet screens) {
        close(owner);

        GuiTransform transform = calculateTransform(owner);
        World world = owner.getWorld();
        Location anchor = toLocation(world, transform.origin());
        List<Entity> spawnedEntities = new ArrayList<>(2);

        try {
            GuiScreen initialScreen = screens.initialScreen();
            TextDisplay title = spawnTitle(
                    toLocation(world, transform.toWorld(initialScreen.titlePosition())),
                    initialScreen);
            spawnedEntities.add(title);

            GuiButton initialButton = initialScreen.button();
            Location buttonLocation = toLocation(world, transform.toWorld(initialButton.position()));
            TextDisplay button = spawnButton(buttonLocation, initialScreen);
            spawnedEntities.add(button);

            for (Entity entity : spawnedEntities) {
                owner.showEntity(this.plugin, entity);
            }

            this.sessions.put(
                    owner.getUniqueId(),
                    new FloatingGuiSession(
                            owner.getUniqueId(),
                            anchor.clone(),
                            transform,
                            screens,
                            initialScreen,
                            title,
                            button));
        } catch (RuntimeException exception) {
            spawnedEntities.forEach(Entity::remove);
            throw exception;
        }
    }

    boolean close(Player owner) {
        return close(owner.getUniqueId());
    }

    void closeAll() {
        Set<UUID> ownerUuids = new HashSet<>(this.sessions.keySet());
        ownerUuids.addAll(this.gridSessions.keySet());
        ownerUuids.forEach(this::close);
    }

    void handleMovement(Player player, Location destination) {
        FloatingGuiSession session = this.sessions.get(player.getUniqueId());
        GridGuiSession gridSession = this.gridSessions.get(player.getUniqueId());
        Location anchor = session != null
                ? session.anchor()
                : gridSession != null
                        ? gridSession.anchor()
                        : null;
        if (anchor == null) {
            return;
        }

        if (!anchor.getWorld().equals(destination.getWorld())
                || anchor.distanceSquared(destination) > MAX_GUI_DISTANCE_SQUARED) {
            close(player.getUniqueId());
        }
    }

    boolean handleClick(Player player) {
        FloatingGuiSession session = this.sessions.get(player.getUniqueId());
        GridGuiSession gridSession = this.gridSessions.get(player.getUniqueId());
        if (session == null && gridSession == null) {
            return false;
        }
        if ((session != null && !session.ownerUuid().equals(player.getUniqueId()))
                || (gridSession != null && !gridSession.ownerUuid().equals(player.getUniqueId()))) {
            return false;
        }

        Location eyeLocation = player.getEyeLocation();
        org.bukkit.util.Vector viewDirection = eyeLocation.getDirection();
        if (gridSession != null) {
            for (GridButtonDisplay button : gridSession.buttons()) {
                if (hitsButton(
                        gridSession.transform(),
                        button.definition().position(),
                        button.definition().label(),
                        eyeLocation,
                        viewDirection)) {
                    player.sendMessage(button.definition().message());
                    return true;
                }
            }
            return false;
        }

        GuiButton button = session.screen().button();
        if (!hitsButton(
                session.transform(),
                button.position(),
                button.label(),
                eyeLocation,
                viewDirection)) {
            return false;
        }

        GuiScreen nextScreen = session.screens().targetScreen(session.screen().button());
        renderScreen(session, nextScreen);
        this.sessions.put(player.getUniqueId(), session.withScreen(nextScreen));
        return true;
    }

    private boolean close(UUID ownerUuid) {
        boolean closed = false;
        FloatingGuiSession session = this.sessions.remove(ownerUuid);
        if (session != null) {
            session.entities().forEach(Entity::remove);
            closed = true;
        }

        GridGuiSession gridSession = this.gridSessions.remove(ownerUuid);
        if (gridSession != null) {
            gridSession.entities().forEach(Entity::remove);
            closed = true;
        }
        return closed;
    }

    private TextDisplay spawnTitle(Location location, GuiScreen screen) {
        return spawnTitle(location, screen.title());
    }

    private TextDisplay spawnTitle(Location location, Component title) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(title);
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        });
    }

    private TextDisplay spawnButton(Location location, GuiScreen screen) {
        return spawnButton(location, screen.button().label());
    }

    private TextDisplay spawnButton(Location location, Component label) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(label);
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(200, 35, 35, 35));
        });
    }

    private void renderScreen(FloatingGuiSession session, GuiScreen screen) {
        World world = session.anchor().getWorld();
        GuiButton button = screen.button();

        session.title().text(screen.title());
        session.title().teleport(toLocation(world, session.transform().toWorld(screen.titlePosition())));
        session.button().text(button.label());
        session.button().teleport(toLocation(world, session.transform().toWorld(button.position())));
    }

    private static boolean hitsButton(
            GuiTransform transform,
            GuiVector localPosition,
            Component label,
            Location eyeLocation,
            org.bukkit.util.Vector viewDirection) {
        return GuiRaycast.hitsButton(
                toGuiVector(eyeLocation),
                toGuiVector(viewDirection),
                transform.toWorld(localPosition),
                transform.right(),
                GuiButtonHitboxCalculator.calculate(label));
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

    private static GuiVector toGuiVector(Location location) {
        return new GuiVector(location.getX(), location.getY(), location.getZ());
    }

    private static GuiVector toGuiVector(org.bukkit.util.Vector vector) {
        return new GuiVector(vector.getX(), vector.getY(), vector.getZ());
    }

    private record FloatingGuiSession(
            UUID ownerUuid,
            Location anchor,
            GuiTransform transform,
            GuiScreenSet screens,
            GuiScreen screen,
            TextDisplay title,
            TextDisplay button) {
        FloatingGuiSession withScreen(GuiScreen newScreen) {
            return new FloatingGuiSession(
                    this.ownerUuid,
                    this.anchor,
                    this.transform,
                    this.screens,
                    newScreen,
                    this.title,
                    this.button);
        }

        List<Entity> entities() {
            return List.of(this.title, this.button);
        }
    }

    private record GridGuiSession(
            UUID ownerUuid,
            Location anchor,
            GuiTransform transform,
            TextDisplay title,
            List<GridButtonDisplay> buttons) {
        List<Entity> entities() {
            List<Entity> entities = new ArrayList<>(this.buttons.size() + 1);
            entities.add(this.title);
            this.buttons.forEach(button -> entities.add(button.display()));
            return entities;
        }
    }

    private record GridButtonDisplay(GuiGridButton definition, TextDisplay display) {}
}
