package syncjam.net.server;

import syncjam.Song;
import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.net.SocketConsumer;
import syncjam.net.client.DataSocketConsumer;

import java.io.*;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ServerDataSocketConsumer extends SocketConsumer
{
    public ServerDataSocketConsumer(InputStream inStream, SongUtilities songUtils)
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
            try
            {
                Song song = (Song) socketObjectReader.readObject();
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
        }
    }
}
