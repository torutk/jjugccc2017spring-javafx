/*
 * © 2017 TAKAHASHI,Toru
 */
package earthgadget;

import java.util.prefs.Preferences;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * 地球が3次元表示で回転するデスクトップガジェット。
 */
public class EarthGadgetApp extends Application {
    private static final double FRAMES_PER_SECOND = 5;
    private static final double AZIMUTH_SPEED_PER_SECOND = 2; // degree
    
    private Translate cameraTranslate = new Translate(0, 0, -300);
    private Rotate cameraRotateY = new Rotate(0, Rotate.Y_AXIS);
    
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
    public void start(Stage primaryStage) {
        Group root = new Group();
        Sphere earth = new Sphere(100);
        root.getChildren().add(earth);
        
        PhongMaterial material = new PhongMaterial();
        Image earthTexture = new Image(getClass().getResourceAsStream("earth.png"));
        material.setDiffuseMap(earthTexture);
        earth.setMaterial(material);
        
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(45);
        camera.setFarClip(1000);
        camera.getTransforms().addAll(cameraRotateY, cameraTranslate);

        // 点光源の定義
        final PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(500);
        pointLight.setTranslateY(0);
        pointLight.setTranslateZ(-500);
        root.getChildren().add(pointLight);

        // 環境光の定義
        AmbientLight ambientLight = new AmbientLight(Color.rgb(192, 192, 192, 0.75));
        root.getChildren().add(ambientLight);
        
        Scene scene = new Scene(root, 300, 250);
        scene.setCamera(camera);

        // ウィンドウ枠の非表示と背景透明化
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        // ドラッグ操作でウィンドウを移動
        scene.setOnMousePressed(event -> {
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - dragStartX);
            primaryStage.setY(event.getScreenY() - dragStartY);
        });
        
        // マウスホイール操作でウィンドウの大きさを変更
        scene.setOnScroll(event -> {
            if (event.isControlDown()) {
                zoom(event.getDeltaY() > 0 ? 1.1 : 0.9, primaryStage);
            }
        });
        // タッチパネルのズーム（ピンチ）操作でウィンドウサイズを変更
        scene.setOnZoom(event -> {
            zoom(event.getZoomFactor(), primaryStage);
        });
        
        // マウス右クリックでポップアップメニューを表示
        ContextMenu popup = createContextMenu(primaryStage);
        root.setOnContextMenuRequested(event -> {
            popup.show(primaryStage, event.getScreenX(), event.getScreenY());
        });
        
        // ウィンドウが終了するときに状態を保存
        primaryStage.setOnCloseRequest(event -> {
            saveStatus(primaryStage);
        });
        
        // 保存した状態があれば復元
        loadStatus(primaryStage);
        
        primaryStage.setTitle("Hello Earth!");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Timeline animation = createAnimation();
        animation.play();
    }

    /**
     * カメラを地球中心に1周回転するアニメーションを作成する。
     * 
     * @return 360度回転するアニメーション
     */
    private Timeline createAnimation() {
        Timeline animation = new Timeline(FRAMES_PER_SECOND);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(cameraRotateY.angleProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(360d / AZIMUTH_SPEED_PER_SECOND),
                        new KeyValue(cameraRotateY.angleProperty(), -360)
                )
        );
        return animation;
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
