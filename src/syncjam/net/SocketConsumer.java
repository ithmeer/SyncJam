package syncjam.net;

import syncjam.SongUtilities;

import java.io.InputStream;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public abstract class SocketConsumer extends InterruptableRunnable
{
    protected final InputStream _inputStream;
    protected final SongUtilities _songUtils;

    public SocketConsumer(InputStream inStream, SongUtilities songUtils)
    {
        _inputStream = inStream;
        _songUtils = songUtils;
    }
}
