package syncjam.net;

import syncjam.SongUtilities;

import java.io.OutputStream;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public abstract class SocketProducer extends InterruptableRunnable
{
    protected final OutputStream _outputStream;
    protected final SongUtilities _songUtils;

    public SocketProducer(OutputStream outStream, SongUtilities songUtils)
    {
        _outputStream = outStream;
        _songUtils = songUtils;
    }
}
