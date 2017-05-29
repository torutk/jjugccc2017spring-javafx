/*
 * © 2017 TAKAHASHI,Toru
 */
package tinygadget;

import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author toru
 */
public class TinyGadgetApp extends Application {

    private TinyGadgetControl gadgetControl;
    
    @Override
    public void start(Stage stage) throws Exception {
        gadgetControl = new TinyGadgetControl(stage, Preferences.userNodeForPackage(this.getClass()));
        Parent root = FXMLLoader.load(getClass().getResource("TinyGadgetView.fxml"));
        
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
