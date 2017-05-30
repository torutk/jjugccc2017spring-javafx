/*
 * © 2017 TAKAHASHI,Toru
 */
package imagegadget;

import com.torutk.javafx.gadget.TinyGadgetSupport;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ImageGadgetアプリケーションのエントリポイントを提供するクラス。
 */
public class ImageGadgetApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ImageGadgetView.fxml"));
        new TinyGadgetSupport(stage, Preferences.userNodeForPackage(this.getClass()));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
