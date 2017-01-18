package syncjam.net.server;

import syncjam.BytesSong;
import syncjam.SongMetadata;
import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.interfaces.Song;
import syncjam.net.NetworkSocket;
import syncjam.net.SocketProducer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ServerDataSocketProducer extends SocketProducer
{
    private final Iterable<ServerSideSocket> _clients;

    public ServerDataSocketProducer(OutputStream outStream, SongUtilities songUtils,
                                    Iterable<ServerSideSocket> clients)
    {
        super(outStream, songUtils);
        _clients = clients;
    }

    @Override
    public void run()
    {
        BlockingQueue<Song> songQueue = _songUtils.getSongQueue();

        while (!terminated)
        {
            try
            {
                Song song = songQueue.take();
                SongMetadata metadata = song.getMetadata();

                // send metadata to all clients
                for (ServerSideSocket client : _clients)
                {
                    ObjectOutputStream socketObjectWriter;

                    try
                    {
                        socketObjectWriter = new ObjectOutputStream(
                                client.getOutputStream(NetworkSocket.SocketType.Data));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        throw new SyncJamException(e.getMessage());
                    }

                    socketObjectWriter.writeObject(metadata);
                    socketObjectWriter.writeInt(100);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new SyncJamException(e.getMessage());
            }
        }

    }
}
