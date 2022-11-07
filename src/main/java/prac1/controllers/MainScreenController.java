/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
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
 * @author Izan Jimenez
 */
public class MainScreenController implements Initializable {

    private final FileChooser fileChooser = new FileChooser();
    private Song song = null;
    private final long MAX_FILE_SIZE = (20480L * 1024L);                        // 20.971.520 Bytes = 20MB 

    //classes per reproduir media(mp3 en aquest cas)
    private Media media;
    private MediaPlayer mediaPlayer;

    //index de numero de la llista de cançcons
    private int songNumber = 0;

    private Timer timer;
    private TimerTask task;
    private boolean running;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Button openBtn;
    Tooltip tooltip = new Tooltip("Carregar cançó");

    @FXML
    private ListView<Song> listView;

    private final ObservableList<Song> songObservableList = FXCollections.observableArrayList();
    private List<String> titles = new ArrayList<>();
    

    /**
     * *
     * Inicialitza el controlador
     *
     * @param url The location used to resolve relative paths for the root
     * object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the
     * root object was not localized.
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
        listView.setPrefHeight(Screen.getPrimary().getBounds().getHeight());    //fem la llista de cancons adaptabele al monitor de la pantalla
        
        progressBar.setPrefWidth(Screen.getPrimary().getBounds().getHeight());
    }

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
            song = new Song(file);                                             // Crear nou objecte de tipus cançó
            song.setPath(file);
                   
            if ( file.exists() ) {                                              // Si s'ha obert un arxiu prèviament...
                
                fileChooser.setInitialDirectory(file.getParentFile());          // ...recorda/obre l'últim directori visitat

                // Genero un índex per a cada element a llistar
                String index = String.format("%02d", listView.getItems().size() + 1);
                song.setIndex(index);
                
                long fileSize = file.length();                
                if ( fileSize <= MAX_FILE_SIZE ) {                              // Filtre: limitar pes arxiu (MAX_FILE_SIZE)
                   
                    if ( !song.getDuration().equals("null") ) {                 // Si l'arxiu té una duració major a 00:00, afegir-la al llistat                       
                        
                        if( !titles.contains(song.getTitle()) ) {
                            songObservableList.add(song);
                            listView.setItems(songObservableList); 
                            titles.add(song.getTitle());
                            songNumber = titles.size();                         //numero de cançcons
                        } else {
                            throw new DuplicatedItemException("Són elements duplicats!");
                        }

                    } else {
                        throw new NoDurationException("Arxiu sense duració!");
                    }

                } else {
                    throw new RuntimeException("Arxiu massa gran!");            // Error personalitzat per limitar tamany d'arxiu (MAX_FILE_SIZE)
                }

            }

        } catch (NullPointerException e) {                                    // Mostra un AVÍS si no se selecciona cap fitxer

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

    @FXML
    private Button playBtn;

    @FXML
    private void playSong() {
        openBtn.setDisable(true);                                               // Deshabilita el botó d'afegir cançons
        System.out.println("index:" + song.getIndex());
        //media = new Media(song.getPath()); 
        
        //media = new Media(Integer.parseInt(songObservableList.get(song.getIndex())));
        //System.out.println(songObservableList.get(0).toString());
        
         //Initialising path of the media file, replace this with your file path   
        String path = song.getPath(); //"/home/javatpoint/Downloads/test.mp3";  
        System.out.println("path:" + path); 
        //Instantiating Media class  
        //media = new Media(new File(path).toString());
        media = new Media("");
        System.out.println("media: "+ media);
          
        //Instantiating MediaPlayer class   
        mediaPlayer = new MediaPlayer(media);  
          
        //by setting this property to true, the audio will be played   
        mediaPlayer.play();  
        //primaryStage.setTitle("Playing Audio");  
        //primaryStage.show();  
        
        
        
    }

    @FXML
    private Button pauseBtn;

    @FXML
    private void pauseSong() {
        openBtn.setDisable(false);                                              // Habilita el botó d'afegir cançons

    }

    @FXML
    private Button stopBtn;

    @FXML
    private void stopSong() {
        openBtn.setDisable(false);                                              // Habilita el botó d'afegir cançons
    }

    @FXML
    private Button btnNextSong;

    @FXML
    void NextSong(ActionEvent event) {

    }

    @FXML
    private Button btnPrevSong;

    @FXML
    void NextSong() {

    }

}
