package syncjam.net;

import java.io.OutputStream;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public abstract class SocketProducer extends InterruptableRunnable
{
    protected final OutputStream _outputStream;

    public SocketProducer(OutputStream outStream)
    {
        _outputStream = outStream;
    }
}
