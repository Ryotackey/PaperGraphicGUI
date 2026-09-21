package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import org.bukkit.inventory.ItemStack;

record GuiIcon(
        String id,
        GuiVector position,
        float width,
        float height,
        ItemStack item) implements GuiComponent {
    GuiIcon {
        GuiComponent.validate(id, position, width, height);
        item = Objects.requireNonNull(item, "item").clone();
    }

    @Override
    public ItemStack item() {
        return this.item.clone();
    }
}
