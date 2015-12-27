package syncjam;

import syncjam.net.NetworkController;
import syncjam.ui.SyncJamUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Main class for SyncJam.
 */
public class SyncJam
{
    private final SyncJamUI mainWindow;

    public SyncJam(int port)
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
        NetworkController netCon = new NetworkController(port, songUtils);
    }

    public static void main(String[] args)
    {
        new SyncJam(25566);
    }
}