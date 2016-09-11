package syncjam.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public abstract class NetworkSocket
{
    protected final Executor _exec;
    protected final List<InputStream> _inputStreams;
    protected final List<OutputStream> _outputStreams;

    public NetworkSocket(Executor exec, List<Socket> sockets) throws IOException
    {
        _exec = exec;

        _inputStreams = new LinkedList<InputStream>();
        _outputStreams = new LinkedList<OutputStream>();

        for (Socket sock : sockets)
        {
            _inputStreams.add(sock.getInputStream());
            _outputStreams.add(sock.getOutputStream());
        }
    }

    public InputStream getInputStream(int i)
    {
        return _inputStreams.get(i);
    }

    public OutputStream getOutputStream(int i)
    {
        return _outputStreams.get(i);
    }

    public String readNextCommand()
    {
        byte[] next = new byte[20];

        try
        {
            getInputStream(0).read(next);
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
            getOutputStream(0).write(cmd);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public abstract void start();
}
