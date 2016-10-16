package syncjam.net.client;

import syncjam.BytesSong;
import syncjam.SongMetadata;
import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.net.SocketProducer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ClientDataSocketProducer extends SocketProducer
{
    public ClientDataSocketProducer(OutputStream outStream, SongUtilities songUtils)
    {
        super(outStream, songUtils);
    }

    @Override
    public void run()
    {
        BlockingQueue<BytesSong> songQueue = _songUtils.getSongQueue();
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
            try
            {
                BytesSong song = songQueue.take();
                SongMetadata metadata = song.getMetadata();
                byte[] data = song.getSongData();

                socketObjectWriter.writeObject(metadata);

                int dataLen = data.length;
                int bytesWritten = 0;
                int chunkSize = 1024;

                socketObjectWriter.write(dataLen);

                while (bytesWritten < dataLen)
                {
                    socketObjectWriter.write(data, bytesWritten,
                                             Math.min(chunkSize, dataLen - bytesWritten));
                    bytesWritten += chunkSize;
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
