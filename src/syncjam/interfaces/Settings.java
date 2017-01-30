package syncjam.interfaces;

import syncjam.utilities.ServerInfo;

import java.util.List;

/**
 * Created by Ithmeer on 1/22/2017.
 */
public interface Settings
{
    void setSavedServers(List<ServerInfo> servers);
    List<ServerInfo> getSavedServers();

    void setUserName(String name);
    String getUserName();

    void setDefaultPort(String port);
    String getDefaultPort();

    void setShowMarker(boolean set);
    boolean getShowMarker();

    void setFollowMarker(boolean set);
    boolean getFollowMarker();

    void saveToDisk();
}
