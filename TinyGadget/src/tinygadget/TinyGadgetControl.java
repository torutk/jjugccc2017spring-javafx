/*
 * © 2017 TAKAHASHI,Toru
 */
package tinygadget;

import java.util.prefs.Preferences;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * ガジェットプログラムに共通する振る舞いをユーティリティとして提供するクラス。
 * 
 * <li>マウスドラッグによるウィンドウの移動
 * <li>Ctrlキー+マウスホイールによるウィンドウの大きさ変更
 * <li>ピンチ操作によるウィンドウの大きさ変更
 * <li>ポップアップメニューからウィンドウ終了
 * <li>終了時に位置・大きさを保存し、起動時に復元
 */
public class TinyGadgetControl {
    private static final int MIN_WIDTH = 128;
    private static final int MIN_HEIGHT = 128;
    
    private Stage stage;
    private Scene scene;
    
    // ドラッグでウィンドウの移動開始時のウィンドウ内マウス座標を保持
    private double dragStartX;
    private double dragStartY;
    // 設定の保存で使用するプリファレンスとキー
    private Preferences prefs;
    private static final String KEY_STAGE_X = "stageX";
    private static final String KEY_STAGE_Y = "stageY";
    private static final String KEY_STAGE_WIDTH = "stageWidth";
    private static final String KEY_STAGE_HEIGHT = "stageHeight";
    
    /**
     * 指定した stage に対してガジェットプログラムの振る舞いを提供する。
     * <p>
     * 本呼び出し時に、まだstageにsceneをセットしていない場合、あるいは後でsceneを差し替えた場合は
     * sceneがセット（差し替え）された時点でsceneに対すて振る舞いを設定する。
     * <p>
     * 本コンストラクタを使用した場合、終了時の状態保存と起動時の復元は行わない。
     * 
     * @param stage ガジェットの振る舞いを提供する対象 stage
     */
    public TinyGadgetControl(Stage stage) {
        this.stage = stage;
        if (scene != null) {
            setup();
        }
        stage.sceneProperty().addListener((obs, ov, nv) -> {
            if (ov != nv && nv != null) {
                scene = nv;
                setup();
            }
        });
        this.stage.initStyle(StageStyle.TRANSPARENT);
    }
    
    /**
     * 指定した stage と prefs を対象としてインスタンス化。
     * 
     * @param stage ガジェットの振る舞いを提供する対象 stage
     * @param prefs ガジェットの状態を保存する preferences
     */
    public TinyGadgetControl(Stage stage, Preferences prefs) {
        this(stage);
        this.prefs = prefs;
    }
    
    /**
     * ガジェットの振る舞いを、stage, scene に設定する。
     */
    protected void setup() {
        scene.setFill(Color.TRANSPARENT);
        setupDragMove();
        setupResize();
        setupContextMenu();
        if (prefs != null) {
            setupStatusResume();
        }
    }
    
    /**
     * コンストラクタで渡された scene 上へのドラッグ操作で stage のウィンドウを移動。
     */
    protected void setupDragMove() {
        scene.setOnMousePressed(event -> {
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - dragStartX);
            stage.setY(event.getScreenY() - dragStartY);
        });
    }
    
    protected void setupResize() {
        // Ctrol + マウスホイール操作でウィンドウの大きさを変更
        scene.setOnScroll(event -> {
            if (event.isControlDown()) {
                zoom(event.getDeltaY() > 0 ? 1.1 : 0.9);
            }
        });
        // タッチパネルのズーム（ピンチ）操作でウィンドウサイズを変更
        scene.setOnZoom(event -> {
            zoom(event.getZoomFactor());
        });
    }
    
    /**
     * 指定した拡大率で指定した stage の大きさを変更する。
     * 
     * @param factor 拡大率（1.0が等倍で、 1.0 より大で大きく、 1.0　より小で小さくする）
     */
    private void zoom(double factor) {
        double nextWidth = Math.max(stage.getWidth() * factor, MIN_WIDTH);
        double nextHeight = Math.max(stage.getHeight() * factor, MIN_HEIGHT);
        
        stage.setWidth(nextWidth);
        stage.setHeight(nextHeight);
    }
    
    protected void setupContextMenu() {
        // マウス右クリックでポップアップメニューを表示
        ContextMenu popup = createContextMenu(stage);
        scene.setOnContextMenuRequested(event -> {
            popup.show(stage, event.getScreenX(), event.getScreenY());
        });
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
    
    protected void setupStatusResume() {
        // ウィンドウが終了するときに状態を保存
        stage.setOnCloseRequest(event -> {
            saveStatus();
        });
        
        // 保存した状態があれば復元
        loadStatus();
    }
    
    /**
     * 状態を永続領域に保存する。
     */
    private void saveStatus() {
        prefs.putInt(KEY_STAGE_X, (int) stage.getX());
        prefs.putInt(KEY_STAGE_Y, (int) stage.getY());
        prefs.putInt(KEY_STAGE_WIDTH, (int) stage.getWidth());
        prefs.putInt(KEY_STAGE_HEIGHT, (int) stage.getHeight());
    }
    
    /**
     * 永続領域に保存された状態を復元する。
     */
    private void loadStatus() {
        stage.setX(prefs.getInt(KEY_STAGE_X, 0));
        stage.setY(prefs.getInt(KEY_STAGE_Y, 0));
        stage.setWidth(prefs.getInt(KEY_STAGE_WIDTH, 320));
        stage.setHeight(prefs.getInt(KEY_STAGE_HEIGHT, 200));
    }
}
