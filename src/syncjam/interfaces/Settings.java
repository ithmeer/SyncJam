package syncjam.interfaces;

import syncjam.utilities.ServerInfo;

import java.util.List;

/**
 * Created by Ithmeer on 1/22/2017.
 */
public interface Settings
{
    void addServer(ServerInfo svr);

    List<ServerInfo> getSavedServers();

    void saveToDisk();
}
