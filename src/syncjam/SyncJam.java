package syncjam;

import syncjam.base.Ticker;
import syncjam.base.Updatable;
import syncjam.ui.SyncJamUI;
import syncjam.ui.WindowObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SyncJam implements Updatable
{
    private SyncJamUI mainWindow = null;

    public static void main(String[] args)
    {
        SyncJam sj = new SyncJam();
    }

    public SyncJam()
    {
        mainWindow = new SyncJamUI();

        /*
        Ticker t = new Ticker(this, 30);
        Thread th = new Thread(t);
        th.start();
        */

        Timer timer = new Timer(1000/60,new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                update();
            }
        });

        timer.setRepeats(true);
        timer.start();


        NowPlaying.setSong(new Song("Spectrum", "Shook", "Spectrum", 324));
        //NowPlaying.setSong(new Song("05 Jam for Jerry.mp3"));
    }

    public void update()
    {
        mainWindow.repaint();
    }
}