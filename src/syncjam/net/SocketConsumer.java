package syncjam.net;

import java.io.InputStream;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public abstract class SocketConsumer extends InterruptableRunnable
{
    protected final InputStream _inputStream;

    public SocketConsumer(InputStream inStream)
    {
        _inputStream = inStream;
    }
}
