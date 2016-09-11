package syncjam;

import syncjam.interfaces.AudioController;
import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.PlayController;
import syncjam.interfaces.Playlist;
import syncjam.net.ConcurrentCommandQueue;
import syncjam.net.ConcurrentSongQueue;
import syncjam.net.SocketNetworkController;
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
        ConcurrentPlayController playCon = new ConcurrentPlayController();
        ConcurrentPlaylist playlist = new ConcurrentPlaylist(playCon);
        ConcurrentCommandQueue cmdQueue = new ConcurrentCommandQueue(playCon, playlist);
        ConcurrentAudioController audioController = new ConcurrentAudioController(playlist, playCon, cmdQueue);
        ConcurrentSongQueue songQueue = new ConcurrentSongQueue();
        playCon.setAudioController(audioController);
        playCon.setCommandQueue(cmdQueue);
        playCon.setPlaylist(playlist);
        playlist.setCommandQueue(cmdQueue);
        SongUtilities songUtils = new SongUtilities(audioController, songQueue, cmdQueue, playCon,
                                                    playlist);
        SocketNetworkController networkCon = new SocketNetworkController(songUtils);

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