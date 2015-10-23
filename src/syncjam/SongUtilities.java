package syncjam;

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

    private final BlockingQueue<String> _commandQueue;

    public SongUtilities()
    {
        _player = new NowPlaying();
        _playlist = new Playlist(_player);
        _commandQueue = new LinkedBlockingQueue<String>();
        _audioController = new AudioController(_playlist, _player);
        _player.setAudioController(_audioController);
    }

    public AudioController getAudioController()
    {
        return _audioController;
    }

    public BlockingQueue<String> getCommandQueue()
    {
        return _commandQueue;
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
