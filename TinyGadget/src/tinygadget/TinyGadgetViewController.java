/*
 * © 2017 TAKAHASHI,Toru
 */
package tinygadget;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

/**
 * 
 * @author toru
 */
public class TinyGadgetViewController implements Initializable {
    @FXML
    private Region root;
    @FXML
    private Circle circle;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        circle.radiusProperty().bind(Bindings.min(root.widthProperty(), root.heightProperty()).divide(2));
    }    
    
}
