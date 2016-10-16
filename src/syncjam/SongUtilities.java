package syncjam;

import syncjam.interfaces.AudioController;
import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.PlayController;
import syncjam.interfaces.Playlist;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Ithmeer on 10/1/2015.
 */
public class SongUtilities
{
    private final Playlist _playlist;
    private final PlayController _player;
    private final AudioController _audioController;
    private final CommandQueue _commandQueue;
    private final BlockingQueue<BytesSong> _songQueue;

    public SongUtilities(AudioController audioCon, BlockingQueue<BytesSong> songQueue,
                         CommandQueue cmdQueue, PlayController playCon, Playlist playlist)
    {
        _audioController = audioCon;
        _commandQueue = cmdQueue;
        _player = playCon;
        _playlist = playlist;
        _songQueue = songQueue;
    }

    public AudioController getAudioController()
    {
        return _audioController;
    }

    public CommandQueue getCommandQueue()
    {
        return _commandQueue;
    }

    public Playlist getPlaylist()
    {
        return _playlist;
    }

    public PlayController getPlayController()
    {
        return _player;
    }

    public BlockingQueue<BytesSong> getSongQueue()
    {
        return _songQueue;
    }
}
