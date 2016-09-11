package syncjam.net.client;

import syncjam.SongUtilities;
import syncjam.net.InterruptableRunnable;
import syncjam.net.SocketConsumer;

import java.io.InputStream;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class DataSocketConsumer extends SocketConsumer
{
    public DataSocketConsumer(InputStream inStream, SongUtilities songUtils)
    {
        super(inStream, songUtils);
    }

    @Override
    public void run()
    {

    }
}
