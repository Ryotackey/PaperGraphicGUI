package io.github.ryotackey.papergraphicgui;

public record GuiVector(double x, double y, double z) {
    public GuiVector {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("GUI vector coordinates must be finite");
        }
    }

    GuiVector add(GuiVector other) {
        return new GuiVector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    GuiVector subtract(GuiVector other) {
        return new GuiVector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    GuiVector multiply(double factor) {
        return new GuiVector(this.x * factor, this.y * factor, this.z * factor);
    }

    GuiVector cross(GuiVector other) {
        return new GuiVector(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x);
    }

    double dot(GuiVector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    GuiVector normalize() {
        double length = Math.sqrt(lengthSquared());
        if (length == 0.0) {
            throw new IllegalStateException("Cannot normalize a zero-length GUI vector");
        }
        return multiply(1.0 / length);
    }
}
