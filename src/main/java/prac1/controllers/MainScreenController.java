/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javax.sound.sampled.UnsupportedAudioFileException;
import prac1.exceptions.NoDurationException;
import prac1.exceptions.DuplicatedItemException;
import prac1.Model.Song;
import prac1.main.SongListViewCell;

/**
 * FXML Controller class
 *
 * @author GrupD
 * @author Txell Llanas
 */
public class MainScreenController implements Initializable {
    
    private final FileChooser fileChooser = new FileChooser();
    private Song song = null;
    private final long MAX_FILE_SIZE = (20480L * 1024L);                        // 20.971.520 Bytes = 20MB 
    
    @FXML
    private Button openBtn;
    Tooltip tooltip = new Tooltip("Carregar cançó");

    @FXML
    private ListView<Song> listView;
    
    private final ObservableList<Song> songObservableList = FXCollections.observableArrayList();
    private List<String> titles = new ArrayList<>();

    
    
    /**
     * (RF01): Obre un 'Dialog' per seleccionar un arxiu *.mp3 dins el Sistema 
     * Operatiu i el llista dins un 'listView'.
     *
     * @author Txell Llanas
     */
    @FXML
    private void openFile() throws UnsupportedAudioFileException, NoDurationException {
        
        try {

            File file = fileChooser.showOpenDialog(null);                       // Obrir 'Dialog' per seleccionar l'arxiu d'àudio
            song = new Song(file);                                              // Crear nou objecte de tipus cançó
                   
            if ( file.exists() ) {                                              // Si s'ha obert un arxiu prèviament...
                
                fileChooser.setInitialDirectory(file.getParentFile());          // ...recorda/obre l'últim directori visitat
        
                // Genero un índex per a cada element a llistar
                String index = String.format( "%02d", listView.getItems().size() + 1 );
                song.setIndex(index);
                
                long fileSize = file.length();                
                if ( fileSize <= MAX_FILE_SIZE ) {                              // Filtre: limitar pes arxiu (MAX_FILE_SIZE)
                   
                    if ( !song.getDuration().equals("null") ) {                 // Si l'arxiu té una duració major a 00:00, afegir-la al llistat                       
                        
                        if( !titles.contains(song.getTitle()) ) {
                            songObservableList.add(song);                        
                            listView.setItems(songObservableList); 
                            titles.add(song.getTitle());
                        } else {
                            throw new DuplicatedItemException("Són elements duplicats!");
                        }
                        
                    } else {
                        throw new NoDurationException("Arxiu sense duració!");
                    }
                    
                } else {
                    throw new RuntimeException("Arxiu massa gran!");             // Error personalitzat per limitar tamany d'arxiu (MAX_FILE_SIZE)
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
            
        } catch (DuplicatedItemException e) {                                   // Mostra un AVÍS quan se selecciona una cançó que ja existeix al llistat
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Avís");
            alert.setContentText("La Cançó seleccionada ja existeix al llistat.");
            alert.show();
            System.out.println("Cançó sense duració");
            
        } catch (NoDurationException e) {
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Avís");
            alert.setContentText("Cançó sense duració: " + e.getLocalizedMessage());
            alert.show();
            System.out.println("Cançó sense duració");
            
        } catch (Exception e) {
            
            System.out.println(e.getMessage());
            
        }  
              
    }

    /***
     * Inicialitza el controlador
     * 
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        openBtn.setTooltip(tooltip);
        
        FileChooser.ExtensionFilter extension;                                  // Filtre: Limitar tipus d'arxiu a MP3
        extension = new FileChooser.ExtensionFilter("MP3-File", "*.mp3");
        fileChooser.getExtensionFilters().add(extension);
        
        Label placeholder = new Label("Afegeix una cançó.");                    // Especifico un texte d'ajuda per quan el llistat està buit
        listView.setPlaceholder(placeholder);        
        
        listView.setItems(songObservableList);                                  // Actualitzo el llistat amb els elements disponibles (openFile())      
        //lambda:  listView.setCellFactory(songListView -> new SongListViewCell());        
        listView.setCellFactory(new Callback<ListView<Song>, ListCell<Song>>() {// Carrego un layout a cada fila del llistat, on carregar-hi les dades de la cançó afegida
            @Override
            public ListCell<Song> call(ListView<Song> songListView) {
            return new SongListViewCell();
            }
        });
        
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
