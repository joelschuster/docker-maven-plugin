package com.ofbizian.plugin.docker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DockerRegistry {
    private static DockerRegistry instance;
    private static boolean skipAutoUnregister;

    private Map<String, ContainerHolder> containers = new HashMap<String, ContainerHolder>(1);

    private DockerRegistry () {
    }

    public static DockerRegistry getInstance() {
        if (instance == null) {
            instance = new DockerRegistry();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        if (!skipAutoUnregister) {
                            getInstance().unregisterAll();
                        }
                    }
                    catch (Exception e) {
                    }
                }
            });
        }
        return instance;
    }

    public synchronized void register(ContainerHolder container) {
        containers.put(container.getImage(), container);
    }

    public synchronized void unregister(String image) {
        try {
            ContainerHolder containerHolder = containers.get(image);
            if (containerHolder != null) {
                containerHolder.stop();
            }
        } catch (Exception e) {
        }
        containers.remove(image);
    }

    public synchronized void unregisterAll() {
        for (Map.Entry<String, ContainerHolder> container : containers.entrySet()) {
            unregister(container.getKey());
        }
    }

    public static void setSkipAutoUnregister(boolean skipAutoUnregister) {
        DockerRegistry.skipAutoUnregister = skipAutoUnregister;
    }
}
