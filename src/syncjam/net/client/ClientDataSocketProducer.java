package syncjam.net.client;

import syncjam.BytesSong;
import syncjam.SongMetadata;
import syncjam.SyncJamException;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Song;
import syncjam.interfaces.SongQueue;
import syncjam.net.SocketProducer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ClientDataSocketProducer extends SocketProducer
{
    private final SongQueue _songQueue;

    public ClientDataSocketProducer(OutputStream outStream, ServiceContainer services)
    {
        super(outStream);
        _songQueue = services.getService(SongQueue.class);
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
            // TODO: log error
            e.printStackTrace();
            throw new SyncJamException(e.getMessage());
        }

        while (!_terminated.get())
        {
            try
            {
                Song song = _songQueue.take();
                SongMetadata metadata = song.getMetadata();
                byte[] data = ((BytesSong) song).getSongData();

                socketObjectWriter.writeObject(metadata);

                int dataLen = data.length;
                int bytesWritten = 0;
                int chunkSize = 1024;

                socketObjectWriter.writeInt(dataLen);

                while (bytesWritten < dataLen)
                {
                    socketObjectWriter.write(data, bytesWritten,
                                             Math.min(chunkSize, dataLen - bytesWritten));
                    bytesWritten += chunkSize;
                }

                socketObjectWriter.flush();
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
