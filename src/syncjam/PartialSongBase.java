package syncjam;

import syncjam.interfaces.PartialSong;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ithmeer on 10/10/2016.
 */
public class PartialSongBase extends SongBase implements PartialSong
{
    private AtomicInteger _progress = new AtomicInteger();
    private AtomicBoolean _isComplete = new AtomicBoolean();

    public PartialSongBase(SongMetadata metadata)
    {
        super(metadata);
    }

    @Override
    public boolean getComplete()
    {
        return _isComplete.get();
    }

    @Override
    public void setComplete()
    {
        _isComplete.set(true);
    }

    @Override
    public int getProgress()
    {
        return _progress.get();
    }

    @Override
    public void setProgress(int value)
    {
        _progress.set(value);
    }
}
