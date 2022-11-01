/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prac1.main;

import prac1.exceptions.noDurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author GrupD
 * @author Txell Llanas
 */
public class Song {

    private final File file;
    private AudioFileFormat fileFormat;
    private String title;
    private String duration;

    public Song(File file) {
        this.file = file;
    }
  
    public String getTitle() {
        int extensio = file.getName().indexOf(".");
        title = file.getName().substring(0, extensio);
        return title;
    }

    public String getDuration() throws UnsupportedAudioFileException, IOException, noDurationException {

            fileFormat = AudioSystem.getAudioFileFormat(file);
            System.out.println("format: " + fileFormat);
        
            if (fileFormat instanceof AudioFileFormat) {
                Map<?, ?> properties = ((AudioFileFormat) fileFormat).properties();
                String key = "duration";
                Long microseconds = (Long) properties.get(key);
                int mili = (int) (microseconds / 1000);
                int sec = (mili / 1000) % 60;
                int min = (mili / 1000) / 60;
                System.out.println("duracio: " + sec +":"+ min);

                if (sec == 0 && min == 0) {
                    throw new noDurationException("La duració de l'arxiu d'àudio"
                                              + " ha de ser superior a 00:00.");
                }

                duration = String.format("%02d:%02d", min, sec);

            } else {
                throw new UnsupportedAudioFileException();
            }
        
        return duration;
    }
}
