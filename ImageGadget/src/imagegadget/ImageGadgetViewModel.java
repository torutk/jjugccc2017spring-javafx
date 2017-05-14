/*
 * © 2017 TAKAHASHI,Toru
 */
package imagegadget;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.image.Image;

/**
 * 指定されたパス（ディレクトリ）以下の画像ファイルから画像（Image）を順次取り出すイテレータのようなクラス。
 * <p>
 * <li>初期化時に、指定パス以下を再帰的に検索し画像ファイルのリストを作成（名前順ソート）
 * <li>nextメソッドで順次画像（Image）オブジェクトを生成して返却
 */
public class ImageGadgetViewModel {

    private Path folder;
    private List<Path> imageFiles;
    private int currentIndex;        
    
    public ImageGadgetViewModel(Path folder) {
        if (Files.isDirectory(folder)) {
            this.folder = folder;
        }
        imageFiles = new ArrayList<Path>();
        folderData();
    }
    
    public Optional<Image> next() {
        if (imageFiles.size() == 0) {
            return Optional.empty();
        } else if (imageFiles.size() <= currentIndex) {
            currentIndex = 0;
        }
        return Optional.ofNullable(loadImage(imageFiles.get(currentIndex++)));
    }

    private Image loadImage(Path path) {
        return new Image(path.toFile().toURI().toString());
    }
    
    private void folderData() {
        try (Stream<Path> stream = Files.walk(folder)) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{bmp,gif,jpeg,jpg,png}");
            imageFiles = stream.filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
