package io.github.ryotackey.papergraphicgui;

final class GuiRaycast {
    private static final double DIRECTION_EPSILON_SQUARED = 1.0E-12;
    private static final double PLANE_PARALLEL_EPSILON = 1.0E-6;

    private GuiRaycast() {}

    static boolean hitsButton(
            GuiVector rayOrigin,
            GuiVector rayDirection,
            GuiVector buttonCenter,
            GuiVector planeRight,
            GuiVector planeUp,
            GuiVector planeNormal,
            GuiButtonHitbox hitbox) {
        if (rayDirection.lengthSquared() < DIRECTION_EPSILON_SQUARED) {
            return false;
        }

        double denominator = rayDirection.dot(planeNormal);
        if (Math.abs(denominator) < PLANE_PARALLEL_EPSILON) {
            return false;
        }

        double distance = buttonCenter.subtract(rayOrigin).dot(planeNormal) / denominator;
        if (distance < 0.0) {
            return false;
        }

        GuiVector hitPoint = rayOrigin.add(rayDirection.multiply(distance));
        GuiVector localHit = hitPoint.subtract(buttonCenter);
        double localX = localHit.dot(planeRight);
        double localY = localHit.dot(planeUp);
        return Math.abs(localX) <= hitbox.width() * 0.5
                && Math.abs(localY) <= hitbox.height() * 0.5;
    }
}
