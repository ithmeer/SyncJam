package syncjam.interfaces;

import syncjam.ConnectionStatus;
import syncjam.SyncJamException;
import syncjam.net.server.ServerSideSocket;

import java.util.Queue;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public interface NetworkController
{
    ConnectionStatus getStatus();

    void setStatus(ConnectionStatus st);

    Queue<ServerSideSocket> getClients();

    boolean isClient();

    void connectToServer(String address, int port, String password) throws SyncJamException;

    void disconnect();

    void startServer(int port, String password) throws SyncJamException;
}
