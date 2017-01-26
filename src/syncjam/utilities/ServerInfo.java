package syncjam.utilities;

/**
 * Created by Ithmeer on 1/22/2017.
 */
public final class ServerInfo
{
    public final String  serverName;
    public final String  ipAddress;
    public final int     port;
    public final String  password;
    public final boolean isHost;

    public ServerInfo(String serverName, String ipAddress, int port, String password)
    {
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.password = password;
        isHost = false;
    }

    public ServerInfo(int port, String password)
    {
        serverName = "";
        ipAddress = "";
        this.port = port;
        this.password = password;
        isHost = true;
    }
}
