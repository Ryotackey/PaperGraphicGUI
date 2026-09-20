package io.github.ryotackey.papergraphicgui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class GuiTransformTest {
    private static final double EPSILON = 1.0E-9;
    private static final GuiVector EYE_POSITION = new GuiVector(10.0, 64.0, 20.0);

    @ParameterizedTest
    @MethodSource("cardinalDirections")
    void transformsLocalCoordinatesForCardinalDirections(
            GuiVector viewDirection, GuiVector expectedOrigin, GuiVector expectedWorldPosition) {
        GuiTransform transform = GuiTransform.fromView(EYE_POSITION, viewDirection, 0.0, 2.5);

        assertVectorEquals(expectedOrigin, transform.origin());
        assertVectorEquals(expectedWorldPosition, transform.toWorld(new GuiVector(0.5, 0.2, 0.0)));
    }

    @Test
    void usesYawWhenLookingStraightUp() {
        GuiTransform transform =
                GuiTransform.fromView(EYE_POSITION, new GuiVector(0.0, 1.0, 0.0), 0.0, 2.5);

        assertVectorEquals(new GuiVector(10.0, 64.0, 22.5), transform.origin());
        assertFinite(transform);
    }

    @Test
    void usesYawWhenLookingStraightDown() {
        GuiTransform transform =
                GuiTransform.fromView(EYE_POSITION, new GuiVector(0.0, -1.0, 0.0), 90.0, 2.5);

        assertVectorEquals(new GuiVector(7.5, 64.0, 20.0), transform.origin());
        assertFinite(transform);
    }

    @Test
    void transformsLocalDepthAlongForwardAxis() {
        GuiTransform transform =
                GuiTransform.fromView(EYE_POSITION, new GuiVector(1.0, 0.0, 0.0), 0.0, 2.5);

        assertVectorEquals(
                new GuiVector(13.25, 64.0, 20.0),
                transform.toWorld(new GuiVector(0.0, 0.0, 0.75)));
    }

    private static Stream<Arguments> cardinalDirections() {
        return Stream.of(
                Arguments.of(
                        new GuiVector(0.0, 0.0, 1.0),
                        new GuiVector(10.0, 64.0, 22.5),
                        new GuiVector(9.5, 64.2, 22.5)),
                Arguments.of(
                        new GuiVector(0.0, 0.0, -1.0),
                        new GuiVector(10.0, 64.0, 17.5),
                        new GuiVector(10.5, 64.2, 17.5)),
                Arguments.of(
                        new GuiVector(1.0, 0.0, 0.0),
                        new GuiVector(12.5, 64.0, 20.0),
                        new GuiVector(12.5, 64.2, 20.5)),
                Arguments.of(
                        new GuiVector(-1.0, 0.0, 0.0),
                        new GuiVector(7.5, 64.0, 20.0),
                        new GuiVector(7.5, 64.2, 19.5)));
    }

    private static void assertFinite(GuiTransform transform) {
        assertAll(
                () -> assertVectorFinite(transform.origin()),
                () -> assertVectorFinite(transform.right()),
                () -> assertVectorFinite(transform.up()),
                () -> assertVectorFinite(transform.forward()),
                () -> assertVectorFinite(transform.toWorld(new GuiVector(0.5, 0.2, 0.0))));
    }

    private static void assertVectorFinite(GuiVector vector) {
        assertTrue(Double.isFinite(vector.x()));
        assertTrue(Double.isFinite(vector.y()));
        assertTrue(Double.isFinite(vector.z()));
    }

    private static void assertVectorEquals(GuiVector expected, GuiVector actual) {
        assertAll(
                () -> assertEquals(expected.x(), actual.x(), EPSILON),
                () -> assertEquals(expected.y(), actual.y(), EPSILON),
                () -> assertEquals(expected.z(), actual.z(), EPSILON));
    }
}
