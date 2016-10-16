package syncjam.net.client;

import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.net.InterruptableRunnable;
import syncjam.net.SocketConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ClientDataSocketConsumer extends SocketConsumer
{
    public ClientDataSocketConsumer(InputStream inStream, SongUtilities songUtils)
    {
        super(inStream, songUtils);
    }

    @Override
    public void run()
    {
        ObjectInputStream socketObjectReader;

        try
        {
            socketObjectReader = new ObjectInputStream(_inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new SyncJamException(e.getMessage());
        }

        while (!terminated)
        {
            int progress = 0;

            do
            {
                try
                {
                    progress = socketObjectReader.readInt();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    throw new SyncJamException(e.getMessage());
                }

                // update progress
            }
            while (progress < 100);

            // finish progress
        }
    }
}
