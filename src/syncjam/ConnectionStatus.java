package syncjam;

/**
 * The current connection status.
 * Created by Ithmeer on 1/18/2017.
 */
public enum ConnectionStatus
{
    /**
     * Not connected or hosted
     */
    Unconnected,

    /**
     * Currently attempting to connect/host/rejoin
     */
    Intermediate,

    /**
     * Successfully connected
     */
    Connected,

    /**
     * Disconnected due to error
     */
    Disconnected,

    /**
     * Successfully hosting server
     */
    Hosted
}
