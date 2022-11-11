/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;
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
 * @author Victor García
 * @author Pablo Morante
 */
public class MainScreenController implements Initializable {

    private final FileChooser fileChooser = new FileChooser();                  // obrir fitxers
    private final long MAX_FILE_SIZE = (20480L * 1024L);                        // 20.971.520 Bytes = 20MB 
    private Song song = null;                                                   // variable per reproduir

    //classes per reproduir media(mp3 en aquest cas)
    private Media media;
    private MediaPlayer mediaPlayer;

    private int songNumber = 0;                                                // index de numero de la llista de cançons

    public void setSongNumber(int index) {
        songNumber = index;
    }

    //control del temps de la cançó
    private Timer timer;
    private TimerTask task;
    private boolean running;

    @FXML
    private Text actualTime;

    @FXML
    private Text songTime;

    @FXML
    private Slider sliderBar;

    @FXML
    private ListView<Song> listView;

    @FXML
    private Slider volumeSlider;

    private final ObservableList<Song> songObservableList = FXCollections.observableArrayList();

    public ObservableList<Song> getSongObservableList() {
        return songObservableList;
    }

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

        openBtn.setTooltip(openTooltip);
        playBtn.setTooltip(playTooltip);
        stopBtn.setTooltip(stopTooltip);
        btnNextSong.setTooltip(nextTooltip);
        btnPrevSong.setTooltip(prevTooltip);
        fwdBtn.setTooltip(fwdTooltip);
        rwdBtn.setTooltip(rwdTooltip);
        randomSong.setTooltip(randomTooltip);
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
        sliderBar.setPrefWidth(Screen.getPrimary().getBounds().getHeight());

//        sliderBar.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
//
//                //mediaPlayer.seek(javafx.util.Duration.seconds(sliderBar.getValue()));
//            }
//        });
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {

                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                }
            }
        });

        currentSongTitle.maxWidth(currentSongTitle.getParent().getScaleX());
        currentSongTitle.prefWidth(Screen.getPrimary().getBounds().getHeight());

    }

    @FXML
    private Button openBtn;
    Tooltip openTooltip = new Tooltip("Carregar cançó (Màx. 20MB)");

    /**
     * (RF01): Permet seleccionar un arxiu *.mp3 dins el Sistema Operatiu i el
     * llista dins una 'listView' connectada a una 'ObservableList'.
     *
     * @author Txell Llanas
     */
    @FXML
    private void openFile() throws UnsupportedAudioFileException, NoDurationException {

        try {

            File file = fileChooser.showOpenDialog(null);                       // Obrir 'Dialog' per seleccionar l'arxiu d'àudio
            song = new Song(file);                                              // Crear nou objecte de tipus cançó
            song.setPath(file);
            listView.refresh();
            System.out.println("song: " + listView.getItems().size());

            if (file.canRead()) {                                               //  Filtre 1: Si l'arxiu existeix i té permissos de lectura...

                fileChooser.setInitialDirectory(file.getParentFile());          // Si s'ha obert un arxiu prèviament, recorda/obre l'últim directori visitat

                String index = String.format("%02d", listView.getItems().size());  // Genero un índex de 2 dígits per a cada element a llistar
                song.setIndex(index);

                long fileSize = file.length();
                if (fileSize <= MAX_FILE_SIZE) {                                // Filtre 2: limitar pes arxiu (MAX_FILE_SIZE)

                    if (!song.getDuration().equals("null")) {                   // Filtre 3: Si l'arxiu té una duració major a 00:00, afegir-la al llistat                       

                        if (!comprovarTitol(songObservableList, song.getTitle())) {

                            songObservableList.add(song);
                            listView.setItems(songObservableList);
                        } else {
                            throw new DuplicatedItemException("Són elements duplicats!");
                        }

                    } else {
                        throw new NoDurationException("Arxiu sense duració!");  // Error personalitzat per indicar que l'arxiu no té duració i no es pot reproduir
                    }

                } else {
                    throw new RuntimeException("Arxiu massa gran!");            // Error personalitzat per limitar tamany d'arxiu (MAX_FILE_SIZE)
                }

            }

        } catch (NullPointerException e) {                                      // Mostra un AVÍS si no se selecciona cap fitxer

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Missatge informatiu");
            alert.setHeaderText("No es pot carregar cap cançó");
            alert.setContentText("No s'ha seleccionat cap arxiu.");
            alert.show();
            System.out.println("S'ha clicat CANCEL·LAR. No s'ha obert cap arxiu");

        } catch (RuntimeException e) {                                          // Mostra un AVÍS si el fitxer supera el tamany màxim d'arxiu (MAX_FILE_SIZE)

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Missatge informatiu");
            alert.setHeaderText("Arxiu massa gran!");
            alert.setContentText("El tamany de l'arxiu excedeix del límit (20MB).");
            alert.show();
            System.out.println("Tamany superior a 20MB");

        } catch (DuplicatedItemException e) {                                   // Mostra un AVÍS quan se selecciona una cançó que ja existeix al llistat

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missatge d'error");
            alert.setHeaderText("La cançó no es pot afegir");
            alert.setContentText("La Cançó seleccionada ja existeix al llistat.");
            alert.show();
            System.out.println("Cançó repetida");

        } catch (NoDurationException e) {                                       // Mostra un AVÍS quan se selecciona una cançó que no té duració (00:00)

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missatge d'error");
            alert.setHeaderText("La cançó no es pot reproduir ");
            alert.setContentText("Cançó sense duració (00:00): "
                    + e.getLocalizedMessage());
            alert.show();
            System.out.println("Cançó sense duració");

        } catch (IOException e) {                                               // Mostra un AVÍS quan no es troba l'arxiu

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avís important");
            alert.setHeaderText("La cançó no es pot reproduir ");
            alert.setContentText("Comprova que la cançó no s'hagi esborrat, "
                    + "canviat d'ubicació o renombrat: " + e.getLocalizedMessage());
            alert.show();
            System.out.println("Arxiu no trobat, IOException: " + e.getMessage());

        } catch (UnsupportedAudioFileException e) {                             // Mostra un AVÍS quan l'arxiu no està realment codificat amb format (*.mp3)

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avís important");
            alert.setHeaderText("La cançó no és un arxiu mp3 vàlid ");
            alert.setContentText("L'arxiu no està correctament codificat o no "
                    + "es tracta d'ún arxiu MP3 (" + e.getLocalizedMessage() + ")");
            alert.show();
            System.out.println("L'arxiu no és un MP3: " + e.getMessage());

        }

    }

    @FXML
    private Button playBtn;
    Tooltip playTooltip = new Tooltip("Reproduir cançó");

    @FXML
    private Label currentSongTitle;

    public void play() {
        playSong();
    }

    @FXML
    private void playSong() {

        try {
            if (openBtn.isDisabled()) {
                openBtn.setDisable(true);
            }
            if (!songObservableList.isEmpty()) {
                if (mediaPlayer != null) {
                    currentSongTitle.setText(songObservableList.get(songNumber).getTitle());
                    System.out.println("currentSongTitle (playSong()): " + currentSongTitle);
                    beginTimer();
                    mediaPlayer.play();
                    running = true;
                } else {
                    song = songObservableList.get(songNumber);
                    media = new Media(song.getPath());
                    mediaPlayer = new MediaPlayer(media);

                    currentSongTitle.setText(songObservableList.get(songNumber).getTitle());
                    beginTimer();
                    mediaPlayer.play();
                    running = true;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * (RF07): Métode per pausar la cançó Pausar la cançó tenint en compte que
     * s'ha de gaurdar el mínut on es pausa amb el currentStatus sabem en quin
     * moment es troba: si s'esta reproduint, la cançó es pausarà si està
     * parada, la cançó continuara reproduint-se
     *
     * @author Pablo Morante
     */
    @FXML
    private void pauseSong() {
        try {
            mediaPlayer.pause();

            Status currentStatus = mediaPlayer.getStatus();

            if (currentStatus == Status.PLAYING) {
                openBtn.setDisable(false);                                              // Habilita el botó d'afegir cançons
                mediaPlayer.pause();
//                playBtn.setDisable(true);
//                pauseBtn.setText("Continue");

                //playPauseImg.setImage(new Image(getClass() + "\\..\\..\\Other Sources\\src\\main\\resources\\icons\\play.png"));
            } else if (currentStatus == Status.PAUSED || currentStatus == Status.STOPPED) {
                openBtn.setDisable(true);
                System.out.println("Player will start at: " + mediaPlayer.getCurrentTime());
                mediaPlayer.play();
//                playBtn.setDisable(false);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avís important");
            alert.setHeaderText("La cançó no es pot pausar.");
            alert.setContentText("Comprova que la cançó no s'hagi esborrat, "
                    + "canviat d'ubicació o renombrat: " + e.getLocalizedMessage());
            alert.show();
            System.out.println("Arxiu no trobat, Exception: " + e.getMessage());
        }

    }

    @FXML
    private Button stopBtn;
    Tooltip stopTooltip = new Tooltip("Parar cançó");

    /**
     * (RF07): Métode per aturar la cançó en aquest cas la cançó atura la
     * reproducció sense guardar a quin minut es troba
     *
     * @author Pablo Morante
     */
    @FXML
    private void stopSong() {
        try {
            openBtn.setDisable(false);                                              // Habilita el botó d'afegir cançons
            mediaPlayer.stop();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avís important");
            alert.setHeaderText("La cançó no es pot aturar.");
            alert.setContentText("Comprova que la cançó no s'hagi esborrat, "
                    + "canviat d'ubicació o renombrat: " + e.getLocalizedMessage());
            alert.show();
            System.out.println("Arxiu no trobat, Exception: " + e.getMessage());
        }
    }

    @FXML
    private Button btnNextSong;
    Tooltip nextTooltip = new Tooltip("Cançó següent");

    @FXML
    public void nextSong() {
        System.out.println("####NextSong:");

        if (songObservableList.size() > 0) {
            if (songNumber <= songObservableList.size()) {                   //si no es la última cançó

                songNumber++;
                System.out.println("SongNumer: " + songNumber);
                //mediaPlayer.stop();
                if (running) {
                    cancelTimer();
                }

                System.out.println("Cançó reproduint:");
                System.out.println(songObservableList.get(songNumber).getTitle());

                song = songObservableList.get(songNumber);
                media = new Media(song.getPath());
                mediaPlayer = new MediaPlayer(media);

                playSong();
            } else {                                                            //si es la ultima
                System.out.println("else");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Avís");
                alert.setContentText("No hi ha més cançons a la llista de reproducció");
                alert.show();
                System.out.println("No hi ha més cançons");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Avís");
            alert.setContentText("No s'ha seleccionat cap arxiu.");
            alert.show();
            System.out.println("CANCEL");
        }
    }

    @FXML
    private Button btnPrevSong;
    Tooltip prevTooltip = new Tooltip("Cançó anterior");

    @FXML
    void prevSong() {
        System.out.println("####PrevSong:");

        if (songObservableList.size() > 0) {
            if (songNumber >= 1) {                                               //si no es la última cançó

                songNumber--;
                System.out.println("SongNumer: " + songNumber);

                //mediaPlayer.stop();
                if (running) {
                    cancelTimer();
                }

                System.out.println("Cançó reproduint:");
                System.out.println(songObservableList.get(songNumber).getTitle());

                song = songObservableList.get(songNumber);
                media = new Media(song.getPath());
                mediaPlayer = new MediaPlayer(media);

                playSong();
            } else {                                                            //si es la ultima
                System.out.println("else");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Avís");
                alert.setContentText("No hi ha més cançons a la llistad de reproducció");
                alert.show();
                System.out.println("No hi ha més cançons");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Avís");
            alert.setContentText("No s'ha seleccionat cap arxiu.");
            alert.show();
            System.out.println("CANCEL");
        }

    }

    @FXML
    private Button fwdBtn;
    Tooltip fwdTooltip = new Tooltip("Avançar 10 seg.");

    @FXML
    void fwdTime() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(10)));
        }
    }

    @FXML
    private Button rwdBtn;
    Tooltip rwdTooltip = new Tooltip("Retrocedir 10 seg.");

    @FXML
    void rwdTime() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(-10)));
        }
    }

    @FXML
    private Button randomSong;
    Tooltip randomTooltip = new Tooltip("Reproducció aleatòria");

    @FXML
    void randomSong() {

    }

    public void beginTimer() {

        timer = new Timer();

        task = new TimerTask() {

            public void run() {

                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                sliderBar.setMajorTickUnit(end);
                sliderBar.setValue((current / end) * 100);

                songTime.setText(String.format("%02.0f:%02.0f", end / 60, end % 60));
                actualTime.setText(String.format("%02.0f:%02.0f", current / 60, current % 60));
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {

        running = false;
        timer.cancel();
    }

    /**
     * (RF05): Creació de mètode per facilitar l'eliminació de cançons aquest
     * mètode comprova Song per Song si el títol es repeteix aixó fa que no
     * calgui una array abans creada 'títols'
     *
     * @author Pablo Morante
     */
    public boolean comprovarTitol(ObservableList<Song> songs, String nouTitol) {
        for (Song song : songs) {
            if (song.getTitle().equals(nouTitol)) {
                return true;
            }

        }
        return false;
    }

    public void playSongRow(Song song) {
        
        System.out.println("currentSongTitle: " + currentSongTitle);
        currentSongTitle.maxWidth(currentSongTitle.getParent().getScaleX());
        currentSongTitle.prefWidth(Screen.getPrimary().getBounds().getHeight());

        try {

            if (!song.getPath().isEmpty()) {

//                pauseBtn.setDisable(false);
//                openBtn.setDisable(true);
                if (mediaPlayer != null) {
                    
                    currentSongTitle.setText(song.getTitle());
                    mediaPlayer.play();
                    running = true;
                    //beginTimer();
                } else {

                    media = new Media(song.getPath());
                    mediaPlayer = new MediaPlayer(media);
                    
                    currentSongTitle.setText(song.getTitle());
                    mediaPlayer.play();
                    running = true;
                    //beginTimer();
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
