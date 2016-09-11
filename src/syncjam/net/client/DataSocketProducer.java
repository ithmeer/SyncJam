package syncjam.net.client;

import syncjam.Song;
import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.net.InterruptableRunnable;
import syncjam.net.SocketProducer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class DataSocketProducer extends SocketProducer
{
    public DataSocketProducer(OutputStream outStream, SongUtilities songUtils)
    {
        super(outStream, songUtils);
    }

    @Override
    public void run()
    {
        ObjectOutputStream socketObjectWriter;
        try
        {
            socketObjectWriter = new ObjectOutputStream(_outputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new SyncJamException(e.getMessage());
        }

        while (!terminated)
        {
            /*
            try
            {
                 socketObjectWriter.writeObject();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new SyncJamException(e.getMessage());
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                throw new SyncJamException(e.getMessage());
            }
            */
        }
    }
}
