/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javax.sound.sampled.UnsupportedAudioFileException;
import prac1.exceptions.noDurationException;
import prac1.main.Song;

/**
 * FXML Controller class
 *
 * @author GrupD
 * @author Txell Llanas
 */
public class MainScreenController implements Initializable {
    
    private FileChooser fileChooser = new FileChooser();
    private String path;
    private Song song = null;
    private final long MAX_FILE_SIZE = (20480L * 1024L);                        // 20.971.520 Bytes = 20MB 
     
    /***
     * Inicialitza el controlador
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @FXML
    private Button openBtn;
    @FXML
    private ListView<String> listView;
    @FXML
    private ObservableList<String> elements = FXCollections.observableArrayList();

    /**
     * (RF01): Obre un 'Dialog' per seleccionar un arxiu *.mp3 dins el Sistema 
     * Operatiu i el llista dins un 'listView'.
     *
     * @author Txell Llanas
     */
    @FXML
    private void openFile() throws UnsupportedAudioFileException, noDurationException {

        try {
            
            FileChooser.ExtensionFilter extension;
            extension = new FileChooser.ExtensionFilter("MP3-File", "*.mp3");   // Filtre: Limitar tipus d'arxiu a MP3
            fileChooser.getExtensionFilters().add(extension);

            File file = fileChooser.showOpenDialog(null);                       // Obrir 'Dialog' per seleccionar l'arxiu d'àudio
            path = file.toURI().toString();
            song = new Song(file);                                              // Crear nou objecte de tipus cançó
        
            if ( file.exists() ) {                                              // Si s'ha obert un arxiu prèviament...
                fileChooser.setInitialDirectory(file.getParentFile());          // ...recorda/obre l'últim directori visitat
            }
        
            if ( path != null ) {                                               // Si hi ha fitxer, llistar-lo
                
                long fileSize = file.length();
                if ( fileSize <= MAX_FILE_SIZE ) {                              // Filtre: limitar pes arxiu (MAX_FILE_SIZE)
                   
                    if ( !song.getDuration().equals("null") ) {
                        elements.add(song.getTitle() +" - "+ song.getDuration());
                        listView.setItems(elements);
                    } else {
                        throw new noDurationException("");
                    }
                    
                } else {
                    throw new RuntimeException("Arxiu massa gran");             // Error personalitzat per limitar tamany d'arxiu (MAX_FILE_SIZE)
                }
            }
            
        } catch ( NullPointerException e ) {                                    // Mostra un AVÍS si no se selecciona cap fitxer
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Avís");
            alert.setContentText("No s'ha seleccionat cap arxiu.");
            alert.show();
            System.out.println("CANCEL");
            
        } catch (RuntimeException e) {                                          // Mostra un AVÍS si el fitxer supera el tamany màxim d'arxiu (MAX_FILE_SIZE)
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Avís");
            alert.setContentText("El tamany de l'arxiu excedeix del límit (20MB).");
            alert.show();
            System.out.println("Tamany superior a 20MB");
            
        } catch (noDurationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Avís");
            alert.setContentText("Cançó sense duració: " + e.getLocalizedMessage());
            alert.show();
            System.out.println("Cançó sense duració");
            
        } catch (Exception e) {
            
            System.out.println(e.getMessage());
            
        }
        
    }    
    
    @FXML
    private Button playBtn;
    
    @FXML
    private void playSong(){
        openBtn.setDisable(true);                                               // Deshabilita el botó d'afegir cançons
        
    }
    
    @FXML
    private Button pauseBtn;
    
    @FXML
    private void pauseSong(){
        openBtn.setDisable(false);                                              // Habilita el botó d'afegir cançons
        
    }
    
    @FXML
    private Button stopBtn;
     
    @FXML
    private void stopSong(){
        openBtn.setDisable(false);                                              // Habilita el botó d'afegir cançons
        
    }   
    
}
