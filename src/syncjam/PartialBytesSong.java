package syncjam;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A partially transmitted song made up of a byte array. Thread-safe.
 * Created by Ithmeer on 10/10/2016.
 */
public class PartialBytesSong extends PartialSongBase
{
    private AtomicReference<byte[]> _songData =  new AtomicReference<byte[]>();
    private Semaphore _gate = new Semaphore(0);

    public PartialBytesSong(SongMetadata metadata)
    {
        super(metadata);
    }

    public byte[] getData()
    {
        try
        {
            _gate.acquire();
        }
        catch (InterruptedException e)
        {
            // TODO: log error
            e.printStackTrace();
        }
        return _songData.get();
    }

    public void setData(byte[] data)
    {
        _songData.set(data);
        _gate.release();
    }
}
