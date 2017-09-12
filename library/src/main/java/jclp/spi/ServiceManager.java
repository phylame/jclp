package jclp.spi;

import jclp.CollectionUtils;
import jclp.io.ResourceUtils;
import jclp.log.Log;
import lombok.NonNull;
import lombok.val;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

public class ServiceManager<T extends ServiceProvider> {
    private final Class<T> serviceType;
    private final ClassLoader classLoader;

    private ServiceLoader<T> serviceLoader;

    private final HashMap<String, T> localRegistry = new HashMap<>();
    private final HashSet<T> serviceSpis = new HashSet<>();

    public ServiceManager(@NonNull Class<T> serviceType) {
        this.serviceType = serviceType;
        this.classLoader = ResourceUtils.getContextLoader();
        init();
    }

    public ServiceManager(@NonNull Class<T> serviceType, ClassLoader loader) {
        this.serviceType = serviceType;
        this.classLoader = loader;
        init();
    }

    protected void init() {
        serviceLoader = AccessController.doPrivileged((PrivilegedAction<ServiceLoader<T>>) () -> classLoader != null
                ? ServiceLoader.load(serviceType, classLoader)
                : ServiceLoader.loadInstalled(serviceType));
        initServices();
    }

    public void reload() {
        serviceSpis.clear();
        localRegistry.clear();
        serviceLoader.reload();
        initServices();
    }

    public Set<T> getServices() {
        val services = new HashSet<T>(serviceSpis);
        services.addAll(localRegistry.values());
        return services;
    }

    public T getService(@NonNull String key) {
        return CollectionUtils.getOrPut(localRegistry, key, false, it -> {
            for (val spi : serviceSpis) {
                val keys = spi.getKeys();
                if (keys != null && keys.contains(it)) {
                    return spi;
                }
            }
            return null;
        });
    }

    public void registerService(@NonNull String name, T factory) {
        localRegistry.put(name, factory);
    }

    private void initServices() {
        val it = serviceLoader.iterator();
        try {
            while (it.hasNext()) {
                try {
                    serviceSpis.add(it.next());
                } catch (ServiceConfigurationError e) {
                    Log.e(getClass().getSimpleName(), "providers.next()", e);
                }
            }
        } catch (ServiceConfigurationError e) {
            Log.e(getClass().getSimpleName(), "providers.hasNext()", e);
        }
    }

}
