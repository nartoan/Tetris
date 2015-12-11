package Sound;

import java.io.*;
import java.util.logging.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Sound extends Thread {
    String audioFilePath;

    /**
     * Play a given audio file.
     * @param audioFilePath Path of the audio file.
     */
    public Sound(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    @Override
    public void run() {
        try {
            File audioFile = new File(audioFilePath);
            Player player = new Player(new FileInputStream(audioFile));
            player.play();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JavaLayerException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
}


