/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prac1.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javax.sound.sampled.UnsupportedAudioFileException;
import prac1.Model.Song;
import prac1.controllers.MainScreenController;
import prac1.exceptions.NoDurationException;

/**
 * Classe que defineix un layout on mostrar les dades de cada cançó carregada al
 * llistat del reproductor.
 *
 * @author Txell Llanas
 */
public class SongListViewCell extends ListCell<Song> {

    @FXML
    private HBox rowLayoutContainer;

    @FXML
    private Button playRowBtn;
    private Tooltip playTooltip = new Tooltip("Reproduir cançó");

    @FXML
    private Label indexLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Button deleteRowBtn;
    private Tooltip deleteTooltip = new Tooltip("Eliminar cançó");

    @FXML
    private Label durationLabel;

    private FXMLLoader fxmlLoader;

    private MainScreenController row = null;
    
    public SongListViewCell() {

    }

    /**
     * Renderitza una fila del llistat de les cançons amb les dades
     * corresponents a cada cançó carregada.
     *
     * @param song Arxiu d'àudio carregat al llistat
     * @param empty Boleà per determinar si hi ha contingut o no dins la fila
     *
     * @author Txell Llanas
     */
    @Override
    protected void updateItem(Song song, boolean empty) {
        super.updateItem(song, empty);

        if (empty || song == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (fxmlLoader == null) {                                           // Carregar layout per cada fila del llistat

                fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/rowLayout.fxml"));
                fxmlLoader.setController(this);

                try {

                    fxmlLoader.load();

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Layout no trobat");
                }

            }
            
            playRowBtn.setTooltip(playTooltip);

            indexLabel.setText(String.valueOf(song.getIndex() + ". "));         // Assigno un índex a la cançó
            System.out.println("ID: " + indexLabel);

            titleLabel.setMinWidth(200);
            titleLabel.setPrefWidth(Screen.getPrimary().getBounds().getHeight());// Prioritat de tamany del titol de la cançó per adaptarla a la finestra
            titleLabel.setText(String.valueOf(song.getTitle()));                // Insereixo el títol de la cançó a l'etiqueta 'indexLabel'
            System.out.println("NOM: " + song.getTitle());

            try {

                durationLabel.setText(String.valueOf(song.getDuration()));      // Insereixo la duració de la cançó a l'etiqueta 'durationLabel'
                durationLabel.setMinWidth(70);
            } catch (UnsupportedAudioFileException | IOException | NoDurationException ex) {
                Logger.getLogger(SongListViewCell.class.getName()).log(Level.SEVERE, null, ex);
            }

            deleteRowBtn.setTooltip(deleteTooltip);
            
            rowLayoutContainer.setAlignment(Pos.CENTER);                        // Forçar aliniament vertical dels elements
            rowLayoutContainer.setStyle("-fx-margin:50px");
            setGraphic(rowLayoutContainer);                                     // Carrego el layout amb les dades a dins
            HBox.setHgrow(rowLayoutContainer, Priority.ALWAYS);    //fem la llista de cancons adaptabele al monitor de la pantalla
            //HBox.setMargin(rowLayoutContainer, new Insets(50,0,50,0));
            try {
                deleteRowBtn.setOnAction(event -> getListView().getItems().remove(getItem()));
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avís important");
                alert.setHeaderText("La cançó no es pot eliminar.");
                alert.setContentText("Comprova que la cançó no s'hagi esborrat, "
                        + "canviat d'ubicació o renombrat: " + e.getLocalizedMessage());
                alert.show();
                System.out.println("Arxiu no trobat, Exception: " + e.getMessage());
            }

            try {
                playRowBtn.setOnAction(event -> {
                    //new MainScreenController().setSongNumber(getListView().getEditingIndex());
                    //new MainScreenController().play();
                });
                //
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avís important");
                alert.setHeaderText("La cançó no es pot eliminar.");
                alert.setContentText("Comprova que la cançó no s'hagi esborrat, "
                        + "canviat d'ubicació o renombrat: " + e.getLocalizedMessage());
                alert.show();
                System.out.println("Arxiu no trobat, Exception: " + e.getMessage());
            }

        }
    }

}
