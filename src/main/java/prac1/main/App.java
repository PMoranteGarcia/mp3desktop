package prac1.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;                                                     // Conté tots els Nodes (organitza l'estructura d'elements gràfics d'una Escena)
import javafx.scene.Scene;                                                      // Escena (conté els controls -components gràfics GUI: Nodes- )
import javafx.stage.Stage;                                                      // Escenari (contenidor -finestra emergent-)

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {                  // 3. Renderitzat de la GUI
        
        try {
            
            String fxml = "stage/mainScreen";

            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource(fxml + ".fxml"));

            Parent p = fxmlLoader.load();

            scene = new Scene(p);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Reproductor MP3");
            primaryStage.setMinWidth(640);
            primaryStage.setMinHeight(480);            
            primaryStage.show();
            
        } catch (IOException ex){
            
             System.out.println("No s'ha pogut carregar la interfície d'Usuari.");
             System.out.println(ex.toString());
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void starter(String[] args) {
        launch();                                                               // 2. Construeix una instància de la classe Application
    }
}