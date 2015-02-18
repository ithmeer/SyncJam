package syncjam;

import syncjam.base.Mouse;
import syncjam.base.Ticker;
import syncjam.base.Updatable;
import syncjam.ui.SyncJamUI;
import syncjam.ui.WindowObject;

import javax.swing.JFrame;
import javax.swing.JPanel;

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

        Ticker t = new Ticker(this, 30);
        Thread th = new Thread(t);
        th.start();
    }

    public void update()
    {
        mainWindow.update();
    }
}
