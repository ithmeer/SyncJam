package syncjam;

import syncjam.interfaces.ServiceContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ithmeer on 1/18/2017.
 */
public class SyncJamServiceContainer implements ServiceContainer
{
    private Map<Class<?>, Object> _services;

    public SyncJamServiceContainer()
    {
        synchronized (this)
        {
            _services = new HashMap<Class<?>, Object>();
        }
    }

    @Override
    public synchronized void addService(Class<?> cls, Object serv)
    {
        if (!cls.isInterface())
        {
            throw new IllegalArgumentException("cls must be an interface");
        }
        else if (!cls.isInstance(serv))
        {
            throw new IllegalArgumentException("serv must be an instance of cls");
        }
        else if (serv == null)
        {
            throw new IllegalArgumentException("serv must be non-null");
        }

        _services.put(cls, serv);
    }

    @Override
    public synchronized <T> T getService(Class<T> cls)
    {
        Object serv = _services.get(cls);
        return serv != null ? (T) serv : null;
    }
}