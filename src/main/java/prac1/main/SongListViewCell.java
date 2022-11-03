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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javax.sound.sampled.UnsupportedAudioFileException;
import prac1.Model.Song;
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

    @FXML
    private Label indexLabel;
    
    @FXML
    private Label titleLabel;

    @FXML
    private Button deleteRowBtn;

    @FXML
    private Label durationLabel;
    
    private FXMLLoader fxmlLoader;
    
    /**
     * Renderitza una fila del llistat de les cançons amb les dades corresponents
     * a cada cançó carregada.
     * 
     * @param song Arxiu d'àudio carregat al llistat
     * @param empty Boleà per determinar si hi ha contingut o no dins la fila
     * 
     * @author Txell Llanas
     */
    @Override
    protected void updateItem(Song song, boolean empty) {
        super.updateItem(song, empty);

        if(empty || song == null) {

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

            indexLabel.setText(String.valueOf(song.getIndex() + ". "));         // Assigno un índex a la cançó
            System.out.println("ID: " + indexLabel);
            
            titleLabel.setText(String.valueOf(song.getTitle()));                // Insereixo el títol de la cançó a l'etiqueta 'indexLabel'
            System.out.println("NOM: "+getTitleLabel());
            
            try {
                
                durationLabel.setText(String.valueOf(song.getDuration()));      // Insereixo la duració de la cançó a l'etiqueta 'durationLabel'
                
            } catch (UnsupportedAudioFileException | IOException | NoDurationException ex) {
               Logger.getLogger(SongListViewCell.class.getName()).log(Level.SEVERE, null, ex);
            }

            rowLayoutContainer.setAlignment(Pos.CENTER);                        // Forçar aliniament vertical dels elements
            setGraphic(rowLayoutContainer);                                     // Carrego el layout amb les dades a dins
        }

    }

    /**
     * Recupera el títol d'una cançó del llistat.
     * 
     * @return Mostra un String amb el títol que conté l'etiqueta 'titleLabel'
     * 
     * @author Txell Llanas
     */
    public String getTitleLabel() {
        String nom = titleLabel.getText();
        return nom;
    }
    
}
