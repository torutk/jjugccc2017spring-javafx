/*
 * © 2017 TAKAHASHI,Toru
 */
package tinygadget;

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
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("TinyGadgetView.fxml"));
        
        Scene scene = new Scene(root, 320, 200);
        
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
        
        // マウス右クリックでポップアップメニューを表示
        ContextMenu popup = createContextMenu(stage);
        root.setOnContextMenuRequested(event -> {
            popup.show(stage, event.getScreenX(), event.getScreenY());
        });
        
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
