package syncjam;

import syncjam.interfaces.PartialSong;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ithmeer on 9/17/2016.
 */
public class SocketSong extends PartialSongBase
{
    private volatile Socket _streamSocket;

    public SocketSong(SongMetadata metadata)
    {
        super(metadata);
    }

    public void setSocket(Socket streamSock)
    {
        _streamSocket = streamSock;
    }

    public Socket getSocket()
    {
        return _streamSocket;
    }
}
