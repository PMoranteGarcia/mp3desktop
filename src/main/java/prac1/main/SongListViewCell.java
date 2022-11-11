/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prac1.main;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private Label indexLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Button deleteRowBtn;
    Tooltip deleteTooltip = new Tooltip("Eliminar cançó");

    @FXML
    private Label durationLabel;

    @FXML
    private Button dwnRowBtn;

    @FXML
    private Button upRowBtn;

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

            int index = Integer.parseInt(song.getIndex()) + 1;
            if (index <= 9) // Assigno un índex a la cançó
            {
                indexLabel.setText(String.valueOf("0" + index + ". "));
            } else {
                indexLabel.setText(String.valueOf(index + ". "));
            }

            System.out.println("ID: " + indexLabel);

            titleLabel.setMinWidth(200);
            indexLabel.setStyle("-fx-font-size:28px;");
            // Ízan, això em surt marcat amb un error, per això ho comento. No sé què li passa... (Txell)
            //titleLabel.setPrefWidth(Screen.getPrimary().getBounds().getHeight());// Prioritat de tamany del titol de la cançó per adaptarla a la finestra

            titleLabel.setText(String.valueOf(song.getTitle()));                // Insereixo el títol de la cançó a l'etiqueta 'indexLabel'
            System.out.println("NOM: " + song.getTitle());

            try {

                durationLabel.setText(String.valueOf(song.getDuration()));      // Insereixo la duració de la cançó a l'etiqueta 'durationLabel'
                durationLabel.setMinWidth(70);
            } catch (UnsupportedAudioFileException | IOException | NoDurationException ex) {
                Logger.getLogger(SongListViewCell.class.getName()).log(Level.SEVERE, null, ex);
            }

            rowLayoutContainer.setAlignment(Pos.CENTER);                        // Forçar aliniament vertical dels elements
            setGraphic(rowLayoutContainer);                                     // Carrego el layout amb les dades a dins
            HBox.setHgrow(rowLayoutContainer, Priority.ALWAYS);                 // Fem la llista de cancons adaptabele al monitor de la pantalla
            try {
                deleteRowBtn.setOnAction(event -> getListView().getItems().remove(getItem()));
                deleteRowBtn.setTooltip(deleteTooltip);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avís important");
                alert.setHeaderText("La cançó no es pot eliminar.");
                alert.setContentText("Comprova que la cançó no s'hagi esborrat, "
                        + "canviat d'ubicació o renombrat: " + e.getLocalizedMessage());
                alert.show();
                System.out.println("Arxiu no trobat, Exception: " + e.getMessage());
            }

            dwnRowBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ObservableList<Song> items = getListView().getItems();
                    int currentIndex = getListView().getSelectionModel().getSelectedIndex();
                    if (currentIndex == items.size() - 1) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Avís important");
                        alert.setHeaderText("La cançó no es pot moure.");
                        alert.setContentText("No es pot moure més cap a abaix, "
                                + "s'ha arribat al final de la llista.");
                        alert.show();
                    } else {
                        Collections.swap(items, currentIndex, currentIndex + 1);
                        items.get(currentIndex).setIndex(String.valueOf(currentIndex));
                        items.get(currentIndex+1).setIndex(String.valueOf(currentIndex+1));
                        getListView().refresh();
                    }
                }
            });

            upRowBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ObservableList<Song> items = getListView().getItems();
                    int currentIndex = getListView().getSelectionModel().getSelectedIndex();
                    if (currentIndex == 0) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Avís important");
                        alert.setHeaderText("La cançó no es pot moure.");
                        alert.setContentText("No es pot moure més cap a adalt, "
                                + "s'ha arribat al principi de la llista.");
                        alert.show();
                    } else {
                        Collections.swap(items, currentIndex, currentIndex - 1);
                        items.get(currentIndex).setIndex(String.valueOf(currentIndex));
                        items.get(currentIndex-1).setIndex(String.valueOf(currentIndex-1));
                        getListView().refresh();
                    }
                }
            });
        }
    }

}
