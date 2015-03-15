package syncjam;

import syncjam.ui.SyncJamUI;
import syncjam.ui.WindowObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SyncJam
{
    private SyncJamUI mainWindow = null;

    public static void main(String[] args)
    {
        SyncJam sj = new SyncJam();
    }

    public SyncJam()
    {
        mainWindow = new SyncJamUI();

        Timer timer = new Timer(1000/60,new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                update();
            }
        });

        timer.setRepeats(true);
        timer.start();

        AudioController auCon = new AudioController();
        NowPlaying.setController(auCon);

        NowPlaying.setSong(new Song("song.mp3"));
        auCon.playSong("song.mp3");
    }
    public void update()
    {
        mainWindow.repaint();
    }
}