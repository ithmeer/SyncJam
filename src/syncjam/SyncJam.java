package syncjam;

import syncjam.ui.SyncJamUI;
import syncjam.ui.WindowObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main class for SyncJam.
 */
public class SyncJam
{
    private final SyncJamUI mainWindow;

    public SyncJam()
    {
        Playlist playlist = new Playlist();
        mainWindow = new SyncJamUI(playlist);

        Timer timer = new Timer(1000/60,new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                mainWindow.repaint();
            }
        });

        timer.setRepeats(true);
        timer.start();

        AudioController auCon = new AudioController(playlist);
        NowPlaying.setController(auCon);
        auCon.start();
    }

    public static void main(String[] args)
    {
        new SyncJam();
    }
}