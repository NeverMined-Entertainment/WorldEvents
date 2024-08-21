package org.nevermined.worldevents.expansions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.wutils.log.Log;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.WorldEventsApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventExpansion;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Singleton
public class ExpansionLoader {

    private final WorldEventsApi api;
    private final ExpansionRegistry expansionRegistry;

    @Inject
    public ExpansionLoader(WorldEventsApi api, ExpansionRegistry expansionRegistry)
    {
        this.api = api;
        this.expansionRegistry = expansionRegistry;
    }

    public void loadExpansions(File expansionFolder)
    {
        File[] jars = expansionFolder.listFiles(((dir, name) -> name.endsWith(".jar")));

        if (jars == null)
            return;

        Map<String, WorldEventAction> newExpansions = new HashMap<>();

        for (File expansionFile : jars)
        {
            for (Class<? extends WorldEventExpansion> expansionClass : findClasses(expansionFile, WorldEventExpansion.class))
            {
                WorldEventExpansion expansion = createExpansionInstance(expansionClass);

                if (expansion.getKey() == null || expansion.getKey().isEmpty()) {
                    Log.global.error("Can not register expansion in class '" + expansionClass.getName() + "' because key is not specified!");
                    continue;
                }
                if (expansion.getAction() == null) {
                    Log.global.error("Can not register expansion in class '" + expansionClass.getName() + "' because action is not specified!");
                    continue;
                }

                newExpansions.put(expansion.getKey(), expansion.getAction());
            }
        }

        expansionRegistry.registerExpansions(newExpansions);
    }

    private <T> List<Class<? extends T>> findClasses(File file, Class<T> clazz) {
        if (!file.exists()) {
            return null;
        }

        URL jar = null;
        try {
            jar = file.toURI().toURL();
        } catch (MalformedURLException e) {
            Log.global.exception("An exception occurred trying to format file to URL", e);
        }
        final URLClassLoader loader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
        final List<String> matches = new ArrayList<>();
        final List<Class<? extends T>> classes = new ArrayList<>();

        try (final JarInputStream stream = new JarInputStream(jar.openStream())) {
            JarEntry entry;
            while ((entry = stream.getNextJarEntry()) != null) {
                final String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }

                matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
            }

            for (final String match : matches) {
                try {
                    final Class<?> loaded = loader.loadClass(match);
                    if (clazz.isAssignableFrom(loaded)) {
                        classes.add(loaded.asSubclass(clazz));
                    }
                } catch (final NoClassDefFoundError | ClassNotFoundException e) {
                    Log.global.exception("An exception occurred trying to find expansion classes", e);
                }
            }

            loader.close();
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to find expansion classes", e);
        }

        return classes;
    }

    @Nullable
    private WorldEventExpansion createExpansionInstance(Class<? extends WorldEventExpansion> clazz)
    {
        try {
            return clazz.getDeclaredConstructor(WorldEventsApi.class).newInstance(api);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Log.global.exception("An exception occurred trying to create '" + clazz.getName() + "' expansion class", e);
        }
        return null;
    }

}
