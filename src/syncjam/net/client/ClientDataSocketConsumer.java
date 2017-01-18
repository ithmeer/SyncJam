package syncjam.net.client;

import syncjam.DatagramChannelSong;
import syncjam.SongMetadata;
import syncjam.SyncJamException;
import syncjam.interfaces.Playlist;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.NetworkSocket;
import syncjam.net.SocketConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by Ithmeer on 6/5/2016.
 */
public class ClientDataSocketConsumer extends SocketConsumer
{
    private NetworkSocket _parent;
    private final Playlist _playlist;

    public ClientDataSocketConsumer(InputStream inStream, ServiceContainer services,
                                    NetworkSocket parent)
    {
        super(inStream);
        _parent = parent;
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

        while (!terminated)
        {
            try
            {
                SongMetadata metadata = (SongMetadata) socketObjectReader.readObject();
                DatagramChannelSong song = new DatagramChannelSong(metadata);
                _playlist.add(song);

                int progress;
                do
                {
                    progress = socketObjectReader.readInt();
                    song.setProgress(progress);
                }
                while (progress < 100);

                song.setComplete();
                song.setStreamChannel(_parent.getStreamChannel());
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
