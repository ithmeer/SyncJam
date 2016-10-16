package syncjam.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public abstract class NetworkSocket
{
    protected final Executor _exec;
    protected final HashMap<SocketType, InputStream> _inputStreams;
    protected final HashMap<SocketType, OutputStream> _outputStreams;

    public enum SocketType { Command, Data, Stream }

    public NetworkSocket(Executor exec, List<Socket> sockets) throws IOException
    {
        _exec = exec;

        _inputStreams = new HashMap<SocketType, InputStream>();
        _outputStreams = new HashMap<SocketType, OutputStream>();

        _inputStreams.put(SocketType.Command, sockets.get(0).getInputStream());
        _outputStreams.put(SocketType.Command, sockets.get(0).getOutputStream());
        _inputStreams.put(SocketType.Data, sockets.get(1).getInputStream());
        _outputStreams.put(SocketType.Data, sockets.get(1).getOutputStream());
        _inputStreams.put(SocketType.Stream, sockets.get(2).getInputStream());
        _outputStreams.put(SocketType.Stream, sockets.get(2).getOutputStream());
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
            e.printStackTrace();
        }
    }

    public abstract void start();
}
