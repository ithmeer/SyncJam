package syncjam.net;

import java.net.Socket;

/**
 * Send and receive client messages.
 * Created by Ithmeer on 3/22/2015.
 */
public class ClientSocket
{
    private final Socket socket;

    public ClientSocket(Socket sock)
    {
        socket = sock;
    }

    public void run()
    {

    }
}
