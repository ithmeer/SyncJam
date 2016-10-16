package syncjam;

/**
 * A partially transmitted song made up of a byte array. Thread-safe.
 * Created by Ithmeer on 10/10/2016.
 */
public class PartialBytesSong extends PartialSongBase
{
    private volatile byte[] _songData;

    public PartialBytesSong(SongMetadata metadata)
    {
        super(metadata);
    }

    public void setData(byte[] data)
    {
        _songData = data;
    }
}
