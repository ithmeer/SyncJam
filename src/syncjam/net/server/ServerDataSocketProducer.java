package syncjam.net.server;

import syncjam.SongMetadata;
import syncjam.SyncJamException;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Song;
import syncjam.interfaces.SongQueue;
import syncjam.net.NetworkSocket;
import syncjam.net.SocketProducer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ServerDataSocketProducer extends SocketProducer
{
    private final Iterable<ServerSideSocket> _clients;
    private final SongQueue _songQueue;

    public ServerDataSocketProducer(OutputStream outStream, ServiceContainer services,
                                    Iterable<ServerSideSocket> clients)
    {
        super(outStream);
        _clients = clients;
        _songQueue = services.getService(SongQueue.class);
    }

    @Override
    public void run()
    {
        while (!terminated)
        {
            try
            {
                Song song = _songQueue.take();
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
                        // TODO: log error
                        e.printStackTrace();
                        throw new SyncJamException(e.getMessage());
                    }

                    socketObjectWriter.writeObject(metadata);
                    socketObjectWriter.writeInt(100);
                    socketObjectWriter.flush();
                }
            }
            catch (Exception e)
            {
                // TODO: log error
                e.printStackTrace();
                throw new SyncJamException(e.getMessage());
            }
        }

    }
}
