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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * FXML Controller class
 *
 * @author manel
 */
public class MainScreenController implements Initializable {
    
    private FileChooser fileChooser = new FileChooser();
    private String path;
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
    
    /** RF01 **/
    @FXML
    private Button openBtn;
    @FXML
    private ListView<String> listView;
    @FXML
    private ObservableList<String> elements = FXCollections.observableArrayList();

    /**
     * *
     * Obre un 'Dialog' per seleccionar un arxiu dins el Sistema Operatiu i 
     * el llista dins un 'listView'. (PUNTS: 0,4)
     *
     */
    @FXML
    private void openFile() throws UnsupportedAudioFileException {

        try {
            
            FileChooser.ExtensionFilter extension;
            extension = new FileChooser.ExtensionFilter("MP3-File", "*.mp3");   // Filtre: Limitar tipus d'arxiu a MP3
            fileChooser.getExtensionFilters().add(extension);

            File song = fileChooser.showOpenDialog(null);                       // Obrir 'Dialog' per seleccionar l'arxiu d'àudio
            path = song.toURI().toString();
            int extensio = song.getName().indexOf(".");
            String name = song.getName().substring(0, extensio);
        
            if ( song.exists() ) {                                              // Si s'ha obert un arxiu prèviament...
                fileChooser.setInitialDirectory(song.getParentFile());          // ...recorda/obre l'últim directori visitat
            }
        
            if ( path != null ) {                                               // Si hi ha fitxer, llistar-lo
                
                long fileSize = song.length();
                if ( fileSize <= MAX_FILE_SIZE ) {                              // Filtre: limitar pes arxiu (MAX_FILE_SIZE)
                    
                    // elements.add(name + " " + getDuration(song));
                    elements.add(name);
                    listView.setItems(elements);
                    
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
            System.out.println("CANCEL");
            
        } catch (Exception e) {
            
            System.out.println(e.getMessage());
            
        }
        
    }
    
    // PENDENT...
    private static float getDuration(File file) throws Exception {
        
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        System.out.println("Format: " + format);
        long audioFileLength = file.length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        float durationInSeconds = (audioFileLength / (frameSize * frameRate));
        return (durationInSeconds);
    }
    
    
    @FXML
    private Button playBtn;
    
    @FXML
    private void playSong(){
        openBtn.setDisable(true);
        
    }
    
    @FXML
    private Button pauseBtn;
    
    @FXML
    private void pauseSong(){
        openBtn.setDisable(false);
        
    }
    
    @FXML
    private Button stopBtn;
     
    @FXML
    private void stopSong(){
        openBtn.setDisable(false);
        
    }   
    
}
