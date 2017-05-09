/*
 * © 2017 TAKAHASHI,Toru
 */
package tinygadget;

import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author toru
 */
public class TinyGadgetApp extends Application {
    // ドラッグ＆ドロップでウィンドウの移動
    private double dragStartX;
    private double dragStartY;
    // 設定の保存で使用するプリファレンスとキー
    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
    private static final String KEY_STAGE_X = "stageX";
    private static final String KEY_STAGE_Y = "stageY";
    private static final String KEY_STAGE_WIDTH = "stageWidth";
    private static final String KEY_STAGE_HEIGHT = "stageHeight";
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("TinyGadgetView.fxml"));
        
        Scene scene = new Scene(root);
        
        // ウィンドウ枠の非表示と背景透明化
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        
        // ドラッグ操作でウィンドウを移動
        scene.setOnMousePressed(event -> {
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - dragStartX);
            stage.setY(event.getScreenY() - dragStartY);
        });
        
        // マウスホイール操作でウィンドウの大きさを変更
        scene.setOnScroll(event -> {
            if (event.isControlDown()) {
                zoom(event.getDeltaY() > 0 ? 1.1 : 0.9, stage);
            }
        });
        
        // タッチパネルのズーム（ピンチ）操作でウィンドウサイズを変更
        scene.setOnZoom(event -> {
            zoom(event.getZoomFactor(), stage);
        });
        
        // マウス右クリックでポップアップメニューを表示
        ContextMenu popup = createContextMenu(stage);
        root.setOnContextMenuRequested(event -> {
            popup.show(stage, event.getScreenX(), event.getScreenY());
        });
        
        // ウィンドウが終了するときに状態を保存
        stage.setOnCloseRequest(event -> {
            saveStatus(stage);
        });
        
        // 保存した状態があれば復元
        loadStatus(stage);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 指定した拡大率で指定した stage の大きさを変更する。
     * 
     * @param factor 拡大率（1.0が等倍で、 1.0 より大で大きく、 1.0　より小で小さくする）
     * @param stage 大きさを変更する対象
     */
    private void zoom(double factor, Stage stage) {
        stage.setWidth(stage.getWidth() * factor);
        stage.setHeight(stage.getHeight() * factor);
    }
    
    /**
     * ポップアップメニューを生成する。
     * 
     * @return ポップアップメニュー
     */
    private ContextMenu createContextMenu(Stage stage) {
        MenuItem exitItem = new MenuItem("終了");
        exitItem.setStyle("-fx-font-size: 2em");
        exitItem.setOnAction(event -> {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        ContextMenu popup = new ContextMenu(exitItem);
        return popup;
    }
    
    /**
     * 状態を永続領域に保存する。
     */
    private void saveStatus(Stage stage) {
        prefs.putInt(KEY_STAGE_X, (int) stage.getX());
        prefs.putInt(KEY_STAGE_Y, (int) stage.getY());
        prefs.putInt(KEY_STAGE_WIDTH, (int) stage.getWidth());
        prefs.putInt(KEY_STAGE_HEIGHT, (int) stage.getHeight());
    }
    
    /**
     * 永続領域に保存された状態を復元する。
     */
    private void loadStatus(Stage stage) {
        stage.setX(prefs.getInt(KEY_STAGE_X, 0));
        stage.setY(prefs.getInt(KEY_STAGE_Y, 0));
        stage.setWidth(prefs.getInt(KEY_STAGE_WIDTH, 320));
        stage.setHeight(prefs.getInt(KEY_STAGE_HEIGHT, 200));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
