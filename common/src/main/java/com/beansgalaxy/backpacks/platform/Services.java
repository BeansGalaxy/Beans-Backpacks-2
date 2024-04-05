package com.beansgalaxy.backpacks.platform;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.platform.services.CompatHelper;
import com.beansgalaxy.backpacks.platform.services.ConfigHelper;
import com.beansgalaxy.backpacks.platform.services.NetworkHelper;
import com.beansgalaxy.backpacks.platform.services.RegistryHelper;

import java.util.ServiceLoader;

public class Services {

    public static final NetworkHelper NETWORK = load(NetworkHelper.class);
    public static final RegistryHelper REGISTRY = load(RegistryHelper.class);
    public static final CompatHelper COMPAT = load(CompatHelper.class);
    public static final ConfigHelper CONFIG = load(ConfigHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}