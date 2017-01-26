package syncjam.interfaces;

import syncjam.ConnectionStatus;
import syncjam.SyncJamException;
import syncjam.net.server.ServerSideSocket;
import syncjam.utilities.ServerInfo;

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

    void connectToServer(ServerInfo serverInfo) throws SyncJamException;

    void disconnect();

    void startServer(ServerInfo serverInfo) throws SyncJamException;
}
