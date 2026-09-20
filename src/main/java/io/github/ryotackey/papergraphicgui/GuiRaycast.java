package io.github.ryotackey.papergraphicgui;

final class GuiRaycast {
    private static final double DIRECTION_EPSILON_SQUARED = 1.0E-12;
    private static final double PLANE_PARALLEL_EPSILON = 1.0E-6;
    private static final GuiVector WORLD_UP = new GuiVector(0.0, 1.0, 0.0);

    private GuiRaycast() {}

    static boolean hitsButton(
            GuiVector rayOrigin,
            GuiVector rayDirection,
            GuiVector buttonCenter,
            GuiVector fallbackRight,
            GuiButtonHitbox hitbox) {
        // CENTER billboard displays rotate per viewer, so the hit plane must use the same
        // camera-relative basis as the player's current view.
        if (rayDirection.lengthSquared() < DIRECTION_EPSILON_SQUARED) {
            return false;
        }

        GuiVector normal = rayDirection.multiply(-1.0).normalize();
        GuiVector right = WORLD_UP.cross(normal);
        if (right.lengthSquared() < DIRECTION_EPSILON_SQUARED) {
            right = fallbackRight;
        } else {
            right = right.normalize();
        }
        GuiVector up = normal.cross(right).normalize();

        double denominator = rayDirection.dot(normal);
        if (Math.abs(denominator) < PLANE_PARALLEL_EPSILON) {
            return false;
        }

        double distance = buttonCenter.subtract(rayOrigin).dot(normal) / denominator;
        if (distance < 0.0) {
            return false;
        }

        GuiVector hitPoint = rayOrigin.add(rayDirection.multiply(distance));
        GuiVector localHit = hitPoint.subtract(buttonCenter);
        double localX = localHit.dot(right);
        double localY = localHit.dot(up);
        return Math.abs(localX) <= hitbox.width() * 0.5
                && Math.abs(localY) <= hitbox.height() * 0.5;
    }
}
