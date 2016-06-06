package syncjam;

import syncjam.ui.SyncJamUI;

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
        SongUtilities songUtils = new SongUtilities();
        mainWindow = new SyncJamUI(songUtils);

        Timer timer = new Timer(1000/60, new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                mainWindow.repaint();
            }
        });

        timer.setRepeats(true);
        timer.start();

        songUtils.getAudioController().start();
    }

    public static void main(String[] args)
    {
        new SyncJam();
    }
}