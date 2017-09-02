package syncjam.net.server;

import syncjam.*;
import syncjam.interfaces.Playlist;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Song;
import syncjam.interfaces.SongQueue;
import syncjam.net.NetworkSocket;
import syncjam.net.SocketConsumer;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ServerDataSocketConsumer extends SocketConsumer
{
    private final Iterable<ServerSideSocket> _clients;
    private final Playlist _playlist;

    public ServerDataSocketConsumer(InputStream inStream, ServiceContainer services,
                                    Iterable<ServerSideSocket> clients)
    {
        super(inStream);
        _clients = clients;
        _playlist = services.getService(Playlist.class);
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


        while (!_terminated.get())
        {
            try
            {
                SongMetadata metadata = (SongMetadata) socketObjectReader.readObject();
                PartialBytesSong song = new PartialBytesSong(metadata);
                _playlist.add(song);

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
                        continue;
                    }

                    socketObjectWriter.writeObject(metadata);
                    socketObjectWriter.writeInt(100);
                    socketObjectWriter.flush();
                }

                int songLength = socketObjectReader.readInt();
                byte[] songData = new byte[songLength];
                byte[] buffer = new byte[1024];
                int bytesRead = 0;

                while (bytesRead < songLength)
                {
                    int read = socketObjectReader.read(buffer);
                    if (read == -1)
                    {
                        // TODO: log error
                        throw new SyncJamException("socket song read error");
                    }

                    for (int i = bytesRead; i < bytesRead + read; i++)
                    {
                        songData[i] = buffer[i - bytesRead];
                    }

                    bytesRead += read;

                    int percentComplete = (int) ((bytesRead / (float) songLength) * 100.0);
                    song.setProgress(percentComplete);

                    // send progress back to all clients
                    /*for (ServerSideSocket client : _clients)
                    {
                        if (_inputStream != client.getInputStream(NetworkSocket.SocketType.Data))
                        {
                            client.getOutputStream(NetworkSocket.SocketType.Data).write(percentComplete);
                        }
                    }*/
                }

                song.setData(songData);
                song.setComplete();
            }
            catch (IOException e)
            {
                // TODO: log error
                e.printStackTrace();
                throw new SyncJamException(e.getMessage());
            }
            catch (ClassNotFoundException e)
            {
                // TODO: log error
                e.printStackTrace();
                throw new SyncJamException(e.getMessage());
            }
        }
    }
}
