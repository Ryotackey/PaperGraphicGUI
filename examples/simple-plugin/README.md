# Simple example plugin

PaperGraphicGUIを別のPaperプラグインから利用する最小サンプルです。`/examplegui` を実行すると、タイトルと2個の矩形ボタンを持つGUIを開きます。

## ビルド

リポジトリのルートで、最初にPaperGraphicGUI本体をビルドします。

```powershell
.\gradlew.bat build
```

続いてサンプルをビルドします。

```powershell
.\gradlew.bat -p examples/simple-plugin build
```

生成された以下の2つのJARをPaper 26.2サーバーの `plugins` に配置してください。

- `build/libs/PaperGraphicGUI-0.1.0-SNAPSHOT.jar`
- `examples/simple-plugin/build/libs/PaperGraphicGUIExample-1.0.0.jar`

サーバー起動後、プレイヤーとして `/examplegui` を実行します。青いボタンをクリックすると、チャットに `Hello from the example plugin!` と表示されます。ツールチップとスニークによる終了も確認できます。

## Callback例外分離の確認

1. 赤い `Throw error` ボタンをクリックします。
2. サーバーログに `Example callback failure` を含むスタックトレースが出ることを確認します。
3. GUIが閉じずに表示されたままであることを確認します。
4. 青い `Click me` ボタンをクリックし、通常のCallbackが引き続き動作することを確認します。
5. 最後にスニークでGUIを閉じられることを確認します。

## Plugin無効化時の確認

GUIを開いた状態で、プレイヤーまたはサーバーコンソールから次を実行します。

```text
/examplegui disable
```

サンプルPlugin自身が無効化され、開いていたGUIが閉じることを確認できます。無効化後にサンプルを再び有効化するには、サーバーを再起動してください。