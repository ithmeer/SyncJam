package syncjam.net;

import syncjam.interfaces.Song;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        _inputStreams = new HashMap<SocketType, InputStream>();
        _outputStreams = new HashMap<SocketType, OutputStream>();

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

    public String readNextCommand()
    {
        byte[] next = new byte[20];

        try
        {
            getInputStream(SocketType.Command).read(next);
        }
        catch (IOException e)
        {
            // TODO: log and report error
            e.printStackTrace();
        }

        return new String(next).trim();
    }

    public void sendCommand(String cmd)
    {
        sendCommand(cmd.getBytes());
    }

    public void sendCommand(byte[] cmd)
    {
        try
        {
            getOutputStream(SocketType.Command).write(cmd);
        }
        catch (IOException e)
        {
            // TODO: log and report error
            e.printStackTrace();
        }
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
