package syncjam;

import syncjam.interfaces.*;
import syncjam.net.ConcurrentCommandQueue;
import syncjam.net.ConcurrentSongQueue;
import syncjam.net.SocketNetworkController;
import syncjam.ui.SyncJamUI;
import syncjam.xml.SyncJamSettings;

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
        ConcurrentPlayController playCon = new ConcurrentPlayController();
        ConcurrentPlaylist playlist = new ConcurrentPlaylist(playCon);
        ConcurrentCommandQueue cmdQueue = new ConcurrentCommandQueue(playCon, playlist);
        ConcurrentAudioController audioController = new ConcurrentAudioController(playlist, playCon,
                                                                                  cmdQueue);
        ConcurrentSongQueue songQueue = new ConcurrentSongQueue();

        playCon.setAudioController(audioController);
        playCon.setCommandQueue(cmdQueue);
        playCon.setPlaylist(playlist);
        playlist.setCommandQueue(cmdQueue);

        SyncJamServiceContainer servCon = new SyncJamServiceContainer();
        servCon.addService(PlayController.class, playCon);
        servCon.addService(Playlist.class, playlist);
        servCon.addService(CommandQueue.class, cmdQueue);
        servCon.addService(SongQueue.class, songQueue);
        servCon.addService(AudioController.class, audioController);

        SocketNetworkController networkCon = new SocketNetworkController(servCon);
        audioController.setNetworkController(networkCon);

        servCon.addService(NetworkController.class, networkCon);

        SyncJamSettings settings = SyncJamSettings.getInstance();
        servCon.addService(Settings.class, settings);

        mainWindow = new SyncJamUI(servCon);

        Timer timer = new Timer(1000/120, event -> mainWindow.repaint());

        timer.setRepeats(true);
        timer.start();

        audioController.start();
    }

    public static void main(String[] args)
    {
        new SyncJam();
    }
}