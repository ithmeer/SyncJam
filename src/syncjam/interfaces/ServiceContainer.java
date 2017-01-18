package syncjam.interfaces;

/**
 * Created by Ithmeer on 1/18/2017.
 */
public interface ServiceContainer
{
    void addService(Class<?> cls, Object serv);

    <T> T getService(Class<T> cls);
}
