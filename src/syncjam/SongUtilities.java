package syncjam;

import syncjam.net.CommandQueue;
import syncjam.net.NetworkController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ithmeer on 10/1/2015.
 */
public class SongUtilities
{
    private final Playlist _playlist;
    private final NowPlaying _player;
    private final AudioController _audioController;
    private final CommandQueue _commandQueue;
    private final NetworkController _networkController;

    public SongUtilities()
    {
        _player = new NowPlaying();
        _playlist = new Playlist(_player);
        _audioController = new AudioController(_playlist, _player);
        _player.setAudioController(_audioController);
        _commandQueue = new CommandQueue(_player, _playlist);
        _player.setCommandQueue(_commandQueue);
        _playlist.setCommandQueue(_commandQueue);
        _networkController = new NetworkController(_commandQueue);
    }

    public AudioController getAudioController()
    {
        return _audioController;
    }

    public CommandQueue getCommandQueue()
    {
        return _commandQueue;
    }

    public NetworkController getNetworkController()
    {
        return _networkController;
    }

    public Playlist getPlaylist()
    {
        return _playlist;
    }

    public NowPlaying getPlayer()
    {
        return _player;
    }
}
