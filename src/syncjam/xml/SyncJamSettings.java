package syncjam.xml;

import syncjam.interfaces.Settings;
import syncjam.utilities.ServerInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ithmeer on 1/22/2017.
 */
@XmlRootElement(name="settings")
public class SyncJamSettings implements Settings
{
    private static final String _savePath = "settings.xml";

    @XmlElement(name="server")
    private List<XmlServerInfo> _servers;
    @XmlElement(name="username")
    private String _userName = "default";
    @XmlElement(name="default_port")
    private String _defaultPort = "9433";
    @XmlElement(name="show_marker")
    private boolean _showMarker = true;
    @XmlElement(name="follow_marker")
    private boolean _followMarker = true;
    @XmlElement(name="minimize_to_tray")
    private boolean _minimizeTray = false;

    private static SyncJamSettings _instance;

    private SyncJamSettings()
    {
        // disallow instantiation of SyncJamSettings outside this class
    }

    public static SyncJamSettings getInstance()
    {
        synchronized (SyncJamSettings.class)
        {
            if (_instance == null)
            {
                try
                {
                    JAXBContext context = JAXBContext.newInstance(SyncJamSettings.class);
                    Unmarshaller depickler = context.createUnmarshaller();
                    _instance = (SyncJamSettings) depickler.unmarshal(
                            new File(_savePath));
                }
                catch (JAXBException e)
                {
                    _instance = new SyncJamSettings();
                }
            }

            return _instance;
        }
    }

    @Override
    @XmlTransient
    public void setSavedServers(List<ServerInfo> servers)
    {
        synchronized (SyncJamSettings.class)
        {
            _servers.clear();
            servers.stream().forEach(s -> _servers.add(new XmlServerInfo(s)));
        }
    }
    @Override
    public List<ServerInfo> getSavedServers()
    {
        synchronized (SyncJamSettings.class)
        {
            return _servers.stream().map(s -> new ServerInfo(s.getName(), s.getIPAddress(),
                                                             s.getPort(), s.getPassword()))
                                    .collect(Collectors.toList());
        }
    }

    @Override
    @XmlTransient
    public void setUserName(String name)
    {
        synchronized (SyncJamSettings.class)
        {
            _userName = name;
        }
    }

    @Override
    public String getUserName()
    {
        synchronized (SyncJamSettings.class)
        {
            return _userName;
        }
    }

    @Override
    @XmlTransient
    public void setDefaultPort(String port)
    {
        synchronized (SyncJamSettings.class)
        {
            _defaultPort = port;
        }
    }

    @Override
    public String getDefaultPort()
    {
        synchronized (SyncJamSettings.class)
        {
            return _defaultPort;
        }
    }

    @Override
    @XmlTransient
    public void setShowMarker(boolean set)
    {
        synchronized (SyncJamSettings.class)
        {
            _showMarker = set;
        }
    }

    @Override
    public boolean getShowMarker()
    {
        synchronized (SyncJamSettings.class)
        {
            return _showMarker;
        }
    }

    @Override
    @XmlTransient
    public void setFollowMarker(boolean set)
    {
        synchronized (SyncJamSettings.class)
        {
            _followMarker = set;
        }
    }

    @Override
    public boolean getFollowMarker()
    {
        synchronized (SyncJamSettings.class)
        {
            return _followMarker;
        }
    }

    @Override
    @XmlTransient
    public void setMinimizeToTray(boolean set)
    {
        synchronized (SyncJamSettings.class)
        {
            _minimizeTray = set;
        }
    }

    @Override
    public boolean getMinimizeToTray()
    {
        synchronized (SyncJamSettings.class)
        {
            return _minimizeTray;
        }
    }

    @Override
    public void saveToDisk()
    {
        synchronized (SyncJamSettings.class)
        {
            try
            {
                JAXBContext context = JAXBContext.newInstance(SyncJamSettings.class);
                Marshaller pickler = context.createMarshaller();
                pickler.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                pickler.marshal(_instance, new File(_savePath));
            }
            catch (JAXBException e)
            {
                e.printStackTrace();
            }
        }
    }
}
