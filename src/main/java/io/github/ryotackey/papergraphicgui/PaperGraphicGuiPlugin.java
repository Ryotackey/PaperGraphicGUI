package io.github.ryotackey.papergraphicgui;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperGraphicGuiPlugin extends JavaPlugin {
    private FloatingGuiManager guiManager;

    @Override
    public void onEnable() {
        this.guiManager = new FloatingGuiManager(this);

        Objects.requireNonNull(getCommand("gui"), "The gui command is missing from plugin.yml")
                .setExecutor(new GuiCommand(this.guiManager));
        getServer().getPluginManager().registerEvents(new FloatingGuiListener(this.guiManager), this);
    }

    @Override
    public void onDisable() {
        if (this.guiManager != null) {
            this.guiManager.shutdown();
        }
    }
}
