/*
 * © 2017 TAKAHASHI,Toru
 */
package imagegadget;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * ImageGadgeViewのコントローラクラス。
 */
public class ImageGadgetViewController implements Initializable {
    
    @FXML
    private Region root;
    @FXML
    private ImageView imageView;
    
    private ImageGadgetViewModel model = new ImageGadgetViewModel(Paths.get("."));
    
    @FXML
    private void handleDragOver(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles() && dragboard.getFiles().get(0).isDirectory()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }
    @FXML
    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles() && dragboard.getFiles().get(0).isDirectory()) {
            model = new ImageGadgetViewModel(Paths.get(dragboard.getFiles().get(0).toURI()));                
            event.setDropCompleted(true);
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty());
        imageView.setPreserveRatio(true);
        
        Timeline timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(10), this::nextImage));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    private ImageGadgetViewModel getModel() {
        return model;
    }

    private void nextImage(Event ev) {
        getModel().next().ifPresent(imageView::setImage);
    }
       
}
