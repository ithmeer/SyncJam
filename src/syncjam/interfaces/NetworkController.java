package syncjam.interfaces;

import syncjam.SyncJamException;
import syncjam.net.server.ServerSideSocket;

import java.util.Queue;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public interface NetworkController
{
    void disconnect();

    Queue<ServerSideSocket> getClients();

    boolean isClient();

    void connectToServer(String address, int port, String password) throws SyncJamException;

    void startServer(int port, String password) throws SyncJamException;
}
