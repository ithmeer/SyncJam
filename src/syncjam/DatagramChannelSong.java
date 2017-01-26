package syncjam;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Ithmeer on 9/17/2016.
 */
public class DatagramChannelSong extends PartialSongBase
{
    private AtomicReference<InputStream> _streamChannel = new AtomicReference<>();
    private CountDownLatch _gate = new CountDownLatch(1);

    public DatagramChannelSong(SongMetadata metadata)
    {
        super(metadata);
    }

    public void setStreamChannel(InputStream channel)
    {
        _streamChannel.set(channel);
        _gate.countDown();
    }

    public InputStream getStreamChannel()
    {
        try
        {
            _gate.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return _streamChannel.get();
    }
}
