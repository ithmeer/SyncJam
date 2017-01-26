package syncjam.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Ithmeer on 1/22/2017.
 */
public class XmlServerInfo
{
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
