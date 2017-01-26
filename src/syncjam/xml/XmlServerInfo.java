package syncjam.xml;

import syncjam.utilities.ServerInfo;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Ithmeer on 1/22/2017.
 */
public class XmlServerInfo
{
    public XmlServerInfo()
    {
    }

    public XmlServerInfo(ServerInfo svr)
    {
        _name = svr.serverName;
        _ip = svr.ipAddress;
        _port = svr.port;
        _password = svr.password;
    }

    @XmlElement(name = "name")
    private String _name;

    @XmlElement(name = "IPAddress")
    private String _ip;

    @XmlElement(name = "port")
    private int _port;

    @XmlElement(name = "password")
    private String _password;

    public String getIPAddress()
    {
        return _ip;
    }

    public String getName()
    {
        return _name;
    }

    public String getPassword()
    {
        return _password;
    }

    public int getPort()
    {
        return _port;
    }
}
