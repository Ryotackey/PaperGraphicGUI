package io.github.ryotackey.papergraphicgui;

final class GuiTransform {
    private static final double DIRECTION_EPSILON_SQUARED = 1.0E-12;
    private static final GuiVector WORLD_UP = new GuiVector(0.0, 1.0, 0.0);

    private final GuiVector origin;
    private final GuiVector right;
    private final GuiVector up;
    private final GuiVector forward;

    private GuiTransform(GuiVector origin, GuiVector right, GuiVector up, GuiVector forward) {
        this.origin = origin;
        this.right = right;
        this.up = up;
        this.forward = forward;
    }

    static GuiTransform fromView(
            GuiVector eyePosition, GuiVector viewDirection, double yawDegrees, double distance) {
        if (!Double.isFinite(yawDegrees)) {
            throw new IllegalArgumentException("GUI yaw must be finite");
        }
        if (!Double.isFinite(distance) || distance < 0.0) {
            throw new IllegalArgumentException("GUI distance must be finite and non-negative");
        }

        GuiVector horizontalForward = new GuiVector(viewDirection.x(), 0.0, viewDirection.z());
        if (horizontalForward.lengthSquared() < DIRECTION_EPSILON_SQUARED) {
            // Yaw provides a stable horizontal direction when pitch removes the view vector's X/Z component.
            double yawRadians = Math.toRadians(yawDegrees);
            horizontalForward = new GuiVector(-Math.sin(yawRadians), 0.0, Math.cos(yawRadians));
        }

        GuiVector forward = horizontalForward.normalize();
        GuiVector right = forward.cross(WORLD_UP).normalize();
        GuiVector origin = eyePosition.add(forward.multiply(distance));
        return new GuiTransform(origin, right, WORLD_UP, forward);
    }

    GuiVector origin() {
        return this.origin;
    }

    GuiVector right() {
        return this.right;
    }

    GuiVector up() {
        return this.up;
    }

    GuiVector forward() {
        return this.forward;
    }

    GuiVector toWorld(GuiVector localPosition) {
        // Local X is screen-right, Y is world-up, and Z points away from the viewer.
        return this.origin
                .add(this.right.multiply(localPosition.x()))
                .add(this.up.multiply(localPosition.y()))
                .add(this.forward.multiply(localPosition.z()));
    }
}
