package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

final class GuiButtonHitboxCalculator {
    private static final PlainTextComponentSerializer PLAIN_TEXT =
            PlainTextComponentSerializer.plainText();
    // TextDisplay renders default-scale text at 40 text pixels per block.
    private static final float PIXELS_PER_BLOCK = 40.0F;
    private static final int TEXT_HEIGHT_PIXELS = 9;
    private static final int LINE_SPACING_PIXELS = 1;
    private static final int HORIZONTAL_BACKGROUND_PADDING_PIXELS = 2;
    private static final int VERTICAL_BACKGROUND_PADDING_PIXELS = 2;

    private GuiButtonHitboxCalculator() {}

    static GuiButtonHitbox calculate(Component label) {
        Objects.requireNonNull(label, "label");
        String plainText = PLAIN_TEXT.serialize(label);
        String[] lines = plainText.split("\\R", -1);

        int maxWidthPixels = 0;
        for (String line : lines) {
            int lineWidthPixels = line.codePoints()
                    .map(GuiButtonHitboxCalculator::glyphAdvancePixels)
                    .sum();
            maxWidthPixels = Math.max(maxWidthPixels, lineWidthPixels);
        }

        int widthPixels = Math.max(1, maxWidthPixels + HORIZONTAL_BACKGROUND_PADDING_PIXELS);
        int heightPixels = lines.length * TEXT_HEIGHT_PIXELS
                + (lines.length - 1) * LINE_SPACING_PIXELS
                + VERTICAL_BACKGROUND_PADDING_PIXELS;
        return new GuiButtonHitbox(widthPixels / PIXELS_PER_BLOCK, heightPixels / PIXELS_PER_BLOCK);
    }

    private static int glyphAdvancePixels(int codePoint) {
        // Paper cannot expose client font resources, so unknown glyphs use the standard 6px advance.
        if (codePoint == ' ') {
            return 4;
        }
        if ("!.,:;'|i".indexOf(codePoint) >= 0) {
            return 2;
        }
        if ("`l".indexOf(codePoint) >= 0) {
            return 3;
        }
        if ("()[]{}It".indexOf(codePoint) >= 0) {
            return 4;
        }
        if ("\"*<>fk".indexOf(codePoint) >= 0) {
            return 5;
        }
        if ("@~".indexOf(codePoint) >= 0) {
            return 7;
        }
        return 6;
    }
}
