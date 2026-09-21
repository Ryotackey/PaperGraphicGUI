package io.github.ryotackey.papergraphicgui.component;

import io.github.ryotackey.papergraphicgui.GuiVector;
import java.util.Objects;
import org.bukkit.inventory.ItemStack;

public record GuiIcon(
        String id,
        GuiVector position,
        float width,
        float height,
        ItemStack item) implements GuiComponent {
    public GuiIcon {
        GuiComponent.validate(id, position, width, height);
        item = Objects.requireNonNull(item, "item").clone();
    }

    @Override
    public ItemStack item() {
        return this.item.clone();
    }
}
