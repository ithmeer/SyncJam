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
        while (!terminated)
        {

            try
            {
                ObjectInputStream socketObjectReader = new ObjectInputStream(_inputStream);
                SongMetadata metadata = (SongMetadata) socketObjectReader.readObject();
                DatagramChannelSong song = new DatagramChannelSong(metadata);

                int progress;
                do
                {
                    progress = socketObjectReader.readInt();
                    song.setProgress(progress);
                }
                while (progress < 100);

                song.setStreamChannel(_parent.getStreamChannel().getInputStream());
                song.setComplete();
                _playlist.add(song);
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
