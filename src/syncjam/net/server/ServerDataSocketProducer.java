package syncjam.net.server;

import syncjam.SongUtilities;
import syncjam.net.SocketProducer;

import java.io.OutputStream;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ServerDataSocketProducer extends SocketProducer
{
    public ServerDataSocketProducer(OutputStream outStream, SongUtilities songUtils)
    {
        super(outStream, songUtils);
    }

    @Override
    public void run()
    {

    }
}
