package syncjam.interfaces;

import syncjam.SyncJamException;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public interface NetworkController
{
    void connectToServer(String address, int port, String password) throws SyncJamException;

    void startServer(int port, String password) throws SyncJamException;
}
