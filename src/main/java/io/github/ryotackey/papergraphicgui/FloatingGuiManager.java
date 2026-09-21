package io.github.ryotackey.papergraphicgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

final class FloatingGuiManager {
    private static final double GUI_DISTANCE = 2.5;
    private static final int CLICK_DEBOUNCE_TICKS = 4;
    private static final GuiVector INPUT_CAPTURE_POSITION = new GuiVector(0.0, -1.0, 0.0);
    private static final float INPUT_CAPTURE_WIDTH = 4.0F;
    private static final float INPUT_CAPTURE_HEIGHT = 2.0F;

    private final Plugin plugin;
    private final Map<UUID, FloatingGuiSession> sessions = new HashMap<>();
    private final Map<UUID, PlayerFreezeState> freezeStates = new HashMap<>();
    private final Map<UUID, Integer> lastHandledClickTicks = new HashMap<>();

    FloatingGuiManager(Plugin plugin) {
        this.plugin = plugin;
    }

    void open(Player owner, GuiScreenSet screens) {
        close(owner);

        GuiTransform transform = calculateTransform(owner);
        World world = owner.getWorld();
        Location anchor = toLocation(world, transform.origin());
        GuiScreen initialScreen = screens.initialScreen();
        List<Entity> spawnedEntities = new ArrayList<>(initialScreen.buttons().size() + 2);

        try {
            TextDisplay title = spawnTitle(
                    toDisplayLocation(
                            world, transform.toWorld(initialScreen.titlePosition()), transform),
                    initialScreen);
            spawnedEntities.add(title);

            List<GuiButtonDisplay> buttons =
                    spawnButtons(world, transform, initialScreen.buttons());
            buttons.forEach(button -> spawnedEntities.add(button.display()));
            Interaction inputCapture = spawnInputCapture(
                    toLocation(world, transform.toWorld(INPUT_CAPTURE_POSITION)));
            spawnedEntities.add(inputCapture);

            for (Entity entity : spawnedEntities) {
                owner.showEntity(this.plugin, entity);
            }

            freezePlayer(owner);
            this.sessions.put(
                    owner.getUniqueId(),
                    new FloatingGuiSession(
                            owner.getUniqueId(),
                            anchor.clone(),
                            transform,
                            screens,
                            initialScreen,
                            title,
                            buttons,
                            inputCapture));
        } catch (RuntimeException exception) {
            this.sessions.remove(owner.getUniqueId());
            restorePlayer(owner.getUniqueId(), owner);
            spawnedEntities.forEach(Entity::remove);
            throw exception;
        }
    }

    boolean close(Player owner) {
        return close(owner.getUniqueId(), owner);
    }

    void closeAll() {
        Set<UUID> ownerUuids = new HashSet<>(this.sessions.keySet());
        ownerUuids.addAll(this.freezeStates.keySet());
        ownerUuids.forEach(this::close);
    }

    boolean isOpen(Player player) {
        return this.sessions.containsKey(player.getUniqueId());
    }

    Location lockedDestination(Player player, Location requestedDestination) {
        PlayerFreezeState state = this.freezeStates.get(player.getUniqueId());
        if (state == null) {
            return null;
        }

        Location destination = requestedDestination.clone();
        destination.setX(state.lockedPosition().x());
        destination.setY(state.lockedPosition().y());
        destination.setZ(state.lockedPosition().z());
        return destination;
    }

    boolean handleClick(Player player) {
        FloatingGuiSession session = this.sessions.get(player.getUniqueId());
        if (session == null || !session.ownerUuid().equals(player.getUniqueId())) {
            return false;
        }

        Location eyeLocation = player.getEyeLocation();
        org.bukkit.util.Vector viewDirection = eyeLocation.getDirection();
        for (GuiButtonDisplay button : session.buttons()) {
            GuiButton definition = button.definition();
            if (!hitsButton(
                    session.transform(),
                    definition.position(),
                    definition.label(),
                    eyeLocation,
                    viewDirection)) {
                continue;
            }
            if (!acquireClick(player.getUniqueId())) {
                return false;
            }

            switch (definition.action()) {
                case GuiButtonAction.Navigate ignored -> {
                    GuiScreen nextScreen = session.screens().targetScreen(definition);
                    this.sessions.put(
                            player.getUniqueId(), renderScreen(player, session, nextScreen));
                }
                case GuiButtonAction.SendMessage message ->
                    player.sendMessage(message.message());
            }
            return true;
        }
        return false;
    }

    boolean handleInteraction(Player player, Entity clickedEntity) {
        UUID ownerUuid = player.getUniqueId();
        FloatingGuiSession session = this.sessions.get(ownerUuid);
        return session != null
                && session.inputCapture().getUniqueId().equals(clickedEntity.getUniqueId())
                && handleClick(player);
    }

    private boolean close(UUID ownerUuid) {
        return close(ownerUuid, Bukkit.getPlayer(ownerUuid));
    }

    private boolean close(UUID ownerUuid, Player owner) {
        boolean closed = false;
        FloatingGuiSession session = this.sessions.remove(ownerUuid);
        if (session != null) {
            session.entities().forEach(Entity::remove);
            closed = true;
        }

        restorePlayer(ownerUuid, owner);
        this.lastHandledClickTicks.remove(ownerUuid);
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

    private TextDisplay spawnButton(Location location, Component label) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            configureEntity(display);
            configureTextDisplay(display);
            display.text(label);
            display.setDefaultBackground(false);
            display.setBackgroundColor(Color.fromARGB(200, 35, 35, 35));
        });
    }

    private List<GuiButtonDisplay> spawnButtons(
            World world, GuiTransform transform, List<GuiButton> definitions) {
        List<GuiButtonDisplay> buttons = new ArrayList<>(definitions.size());
        try {
            for (GuiButton definition : definitions) {
                TextDisplay display = spawnButton(
                        toDisplayLocation(
                                world, transform.toWorld(definition.position()), transform),
                        definition.label());
                buttons.add(new GuiButtonDisplay(definition, display));
            }
            return buttons;
        } catch (RuntimeException exception) {
            buttons.forEach(button -> button.display().remove());
            throw exception;
        }
    }

    private Interaction spawnInputCapture(Location location) {
        return location.getWorld().spawn(location, Interaction.class, interaction -> {
            configureEntity(interaction);
            interaction.setInteractionWidth(INPUT_CAPTURE_WIDTH);
            interaction.setInteractionHeight(INPUT_CAPTURE_HEIGHT);
            interaction.setResponsive(true);
        });
    }

    private FloatingGuiSession renderScreen(
            Player owner, FloatingGuiSession session, GuiScreen screen) {
        World world = session.anchor().getWorld();
        List<GuiButtonDisplay> buttons =
                spawnButtons(world, session.transform(), screen.buttons());
        try {
            buttons.forEach(button -> owner.showEntity(this.plugin, button.display()));
            session.title().text(screen.title());
            session.title().teleport(toDisplayLocation(
                    world, session.transform().toWorld(screen.titlePosition()), session.transform()));
            session.buttons().forEach(button -> button.display().remove());
            return session.withScreen(screen, buttons);
        } catch (RuntimeException exception) {
            buttons.forEach(button -> button.display().remove());
            throw exception;
        }
    }

    private boolean acquireClick(UUID ownerUuid) {
        int currentTick = Bukkit.getCurrentTick();
        Integer previousTick = this.lastHandledClickTicks.get(ownerUuid);
        if (previousTick != null && currentTick - previousTick < CLICK_DEBOUNCE_TICKS) {
            return false;
        }
        this.lastHandledClickTicks.put(ownerUuid, currentTick);
        return true;
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
                transform.up(),
                transform.forward(),
                GuiButtonHitboxCalculator.calculate(label));
    }

    private static void configureEntity(Entity entity) {
        entity.setVisibleByDefault(false);
        entity.setPersistent(false);
    }

    private static void configureTextDisplay(TextDisplay display) {
        display.setBillboard(Display.Billboard.FIXED);
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

    private static Location toDisplayLocation(
            World world, GuiVector position, GuiTransform transform) {
        Location location = toLocation(world, position);
        location.setYaw((float) transform.displayYawDegrees());
        location.setPitch(0.0F);
        return location;
    }

    private void freezePlayer(Player player) {
        UUID ownerUuid = player.getUniqueId();
        Location location = player.getLocation();
        this.freezeStates.put(
                ownerUuid,
                new PlayerFreezeState(
                        player.hasGravity(),
                        player.getWalkSpeed(),
                        player.getFlySpeed(),
                        new GuiVector(location.getX(), location.getY(), location.getZ())));
        player.setVelocity(new Vector());
        player.setFallDistance(0.0F);
        player.setGravity(false);
        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
    }

    private void restorePlayer(UUID ownerUuid, Player player) {
        PlayerFreezeState state = this.freezeStates.remove(ownerUuid);
        if (state == null || player == null) {
            return;
        }

        player.setGravity(state.gravityEnabled());
        player.setWalkSpeed(state.walkSpeed());
        player.setFlySpeed(state.flySpeed());
        player.setVelocity(new Vector());
        player.setFallDistance(0.0F);
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
            List<GuiButtonDisplay> buttons,
            Interaction inputCapture) {
        FloatingGuiSession withScreen(
                GuiScreen newScreen, List<GuiButtonDisplay> newButtons) {
            return new FloatingGuiSession(
                    this.ownerUuid,
                    this.anchor,
                    this.transform,
                    this.screens,
                    newScreen,
                    this.title,
                    newButtons,
                    this.inputCapture);
        }

        List<Entity> entities() {
            List<Entity> entities = new ArrayList<>(this.buttons.size() + 2);
            entities.add(this.title);
            this.buttons.forEach(button -> entities.add(button.display()));
            entities.add(this.inputCapture);
            return entities;
        }
    }

    private record GuiButtonDisplay(GuiButton definition, TextDisplay display) {}

    private record PlayerFreezeState(
            boolean gravityEnabled,
            float walkSpeed,
            float flySpeed,
            GuiVector lockedPosition) {}
}
