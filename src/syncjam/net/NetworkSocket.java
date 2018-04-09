package syncjam.net;

import syncjam.interfaces.Song;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public abstract class NetworkSocket
{
    protected final Executor _exec;
    private final SocketAddress _ipAddress;
    protected final HashMap<SocketType, InputStream> _inputStreams;
    protected final HashMap<SocketType, OutputStream> _outputStreams;
    protected final Socket _streamChannel;
    private final List<Socket> _sockets;

    public enum SocketType { Command, Data }

    public NetworkSocket(Executor exec, List<Socket> sockets, Socket streamSock,
                         SocketAddress ipAddress) throws IOException
    {
        _exec = exec;
        _ipAddress = ipAddress;

        _inputStreams = new HashMap<>();
        _outputStreams = new HashMap<>();

        _inputStreams.put(SocketType.Command, sockets.get(0).getInputStream());
        _outputStreams.put(SocketType.Command, sockets.get(0).getOutputStream());
        _inputStreams.put(SocketType.Data, sockets.get(1).getInputStream());
        _outputStreams.put(SocketType.Data, sockets.get(1).getOutputStream());

        _streamChannel = streamSock;
        _sockets = sockets;
    }

    public SocketAddress getIPAddress()
    {
        return _ipAddress;
    }

    public Socket getStreamChannel()
    {
        return _streamChannel;
    }

    public InputStream getInputStream(SocketType type)
    {
        return _inputStreams.get(type);
    }

    public OutputStream getOutputStream(SocketType type)
    {
        return _outputStreams.get(type);
    }

    public CommandPacket readNextCommand() throws IOException, ClassNotFoundException
    {
        ObjectInputStream socketObjectReader = new ObjectInputStream(
                getInputStream(SocketType.Command));
        return (CommandPacket) socketObjectReader.readObject();
    }

    public void sendCommand(CommandPacket packet) throws IOException
    {
        ObjectOutputStream cmdStream = new ObjectOutputStream(getOutputStream(SocketType.Command));
        cmdStream.writeObject(packet);
    }

    public abstract void start();

    public void stop()
    {
        for (Socket s : _sockets)
        {
            try
            {
                s.close();
            }
            catch (IOException e)
            {
                // TODO: log error
                e.printStackTrace();
            }
        }
    }
}
