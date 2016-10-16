package syncjam.net.server;

import syncjam.*;
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

    public ServerDataSocketConsumer(InputStream inStream, SongUtilities songUtils,
                                    Iterable<ServerSideSocket> clients)
    {
        super(inStream, songUtils);
        _clients = clients;
    }

    @Override
    public void run()
    {
        BlockingQueue<BytesSong> songQueue = _songUtils.getSongQueue();
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
                SongMetadata metadata = (SongMetadata) socketObjectReader.readObject();
                PartialBytesSong song = new PartialBytesSong(metadata);

                int songLength = socketObjectReader.readInt();
                byte[] songData = new byte[songLength];
                byte[] buffer = new byte[1024];
                int bytesRead = 0;

                while (bytesRead < songLength)
                {
                    int read = socketObjectReader.read(buffer);
                    if (read == -1)
                    {
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
                    for (ServerSideSocket client : _clients)
                    {
                        if (_inputStream != client.getInputStream(NetworkSocket.SocketType.Data))
                        {
                            client.getOutputStream(NetworkSocket.SocketType.Data).write(percentComplete);
                        }
                    }
                }

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
