package syncjam;

import java.nio.channels.ReadableByteChannel;

/**
 * Created by Ithmeer on 9/17/2016.
 */
public class DatagramChannelSong extends PartialSongBase
{
    private volatile ReadableByteChannel _streamChannel;

    public DatagramChannelSong(SongMetadata metadata)
    {
        super(metadata);
    }

    public void setStreamChannel(ReadableByteChannel channel)
    {
        _streamChannel = channel;
    }

    public ReadableByteChannel getStreamChannel()
    {
        return _streamChannel;
    }
}
