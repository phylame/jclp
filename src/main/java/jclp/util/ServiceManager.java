/*
 * Copyright 2017 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jclp.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import jclp.log.Log;
import lombok.NonNull;
import lombok.val;

public class ServiceManager<T extends NamedService> {
    private static final String TAG = "ServiceManager";

    private final Class<T> serviceType;
    private final ClassLoader classLoader;
    private ServiceLoader<T> serviceLoader;
    private Map<String, T> nameRegistry;
    private Set<T> serviceSpis;

    public ServiceManager(@NonNull Class<T> serviceType) {
        this.serviceType = serviceType;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        init();
    }

    public ServiceManager(@NonNull Class<T> serviceType, ClassLoader loader) {
        this.serviceType = serviceType;
        this.classLoader = loader;
        init();
    }

    protected void init() {
        serviceSpis = new HashSet<>();
        serviceLoader = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<T>>() {
            @Override
            public ServiceLoader<T> run() {
                return classLoader != null
                        ? ServiceLoader.load(serviceType, classLoader)
                        : ServiceLoader.loadInstalled(serviceType);
            }
        });
        initServices();
        nameRegistry = new HashMap<>();
    }

    public void reload() {
        serviceSpis.clear();
        nameRegistry.clear();
        serviceLoader.reload();
        initServices();
    }

    public Set<T> getServices() {
        val services = new HashSet<T>(serviceSpis);
        services.addAll(nameRegistry.values());
        return services;
    }

    public T getService(@NonNull String name) {
        val service = nameRegistry.get(name);
        if (service != null) {
            return service;
        }
        for (val spi : serviceSpis) {
            val names = spi.getNames();
            if (names != null && names.contains(name)) {
                return spi;
            }
        }
        return null;
    }

    public void registerService(@NonNull String name, T factory) {
        nameRegistry.put(name, factory);
    }

    private void initServices() {
        val it = serviceLoader.iterator();
        try {
            while (it.hasNext()) {
                try {
                    serviceSpis.add(it.next());
                } catch (ServiceConfigurationError e) {
                    Log.e(TAG, "providers.next()", e);
                }
            }
        } catch (ServiceConfigurationError e) {
            Log.e(TAG, "providers.hasNext()", e);
        }
    }
}
