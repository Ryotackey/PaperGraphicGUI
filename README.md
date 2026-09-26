# PaperGraphicGUI

Paper 26.2 / Java 25向けのworld-space GUIプラグインです。Display Entityを利用し、他のPaperプラグインから画面を定義して表示できます。

## 依存関係

現時点ではMavenリポジトリへ公開していないため、ビルド済みJARを利用側プロジェクトの `libs` に配置します。

```kotlin
dependencies {
    compileOnly(files("libs/PaperGraphicGUI-0.1.0-SNAPSHOT.jar"))
    compileOnly("io.papermc.paper:paper-api:26.2.build.126-stable")
}
```

利用側の `plugin.yml` では、APIを使用する前にPaperGraphicGUIが有効になるよう依存関係を宣言します。

```yaml
depend:
  - PaperGraphicGUI
```

## 最小利用例

```java
import io.github.ryotackey.papergraphicgui.GuiVector;
import io.github.ryotackey.papergraphicgui.api.FloatingGuiApi;
import io.github.ryotackey.papergraphicgui.component.GuiAction;
import io.github.ryotackey.papergraphicgui.component.GuiRectangle;
import io.github.ryotackey.papergraphicgui.screen.GuiScreen;
import io.github.ryotackey.papergraphicgui.screen.GuiScreenSet;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExamplePlugin extends JavaPlugin {
    private FloatingGuiApi guiApi;

    @Override
    public void onEnable() {
        this.guiApi = Bukkit.getServicesManager().load(FloatingGuiApi.class);
        if (this.guiApi == null) {
            throw new IllegalStateException("PaperGraphicGUI service is unavailable");
        }
    }

    public void openMenu(Player player) {
        GuiRectangle action = new GuiRectangle(
                "custom-action",
                new GuiVector(0.0, -0.15, 0.0),
                1.4F,
                0.4F,
                Material.BLUE_CONCRETE,
                Component.text("Run"),
                new GuiAction.Callback(clickedPlayer ->
                        clickedPlayer.sendMessage(Component.text("Custom action!"))));

        GuiScreen screen = new GuiScreen(
                "main",
                Component.text("Example Menu"),
                new GuiVector(0.0, 0.4, 0.0),
                List.of(action));
        GuiScreenSet screens = new GuiScreenSet(screen.id(), List.of(screen));

        this.guiApi.open(this, player, screens);
    }
}
```

`open` に渡したPluginが無効化されると、そのPluginが所有するGUIは自動的に閉じられます。同じプレイヤーへ別のGUIを開いた場合は、既存GUIを安全に閉じて置き換えます。

独自CallbackはPaperのメインスレッド上で実行されます。Callbackが実行時例外を投げた場合、PaperGraphicGUIはスタックトレースをログへ記録し、GUI Sessionを維持します。

