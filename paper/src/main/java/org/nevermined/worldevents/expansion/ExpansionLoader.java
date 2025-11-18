package org.nevermined.worldevents.expansion;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.inject.Qualifier;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Singleton
public class ExpansionLoader {

    @Qualifier
    @Target({ FIELD, PARAMETER, METHOD })
    @Retention(RUNTIME)
    public @interface ExpansionDirectory {}

    private final ExpansionRegistryApi expansionRegistry;
    private final File defaultExpansionDirectory;

    private final Set<String> cachedClassMatches = new HashSet<>();

    @Inject
    public ExpansionLoader(ExpansionRegistryApi expansionRegistry, @ExpansionDirectory File expansionDirectory)
    {
        this.expansionRegistry = expansionRegistry;
        this.defaultExpansionDirectory = expansionDirectory;
    }

    public void reloadExpansions()
    {
        expansionRegistry.reloadExpansions();
        loadExpansions(defaultExpansionDirectory);
    }

    public void loadExpansions(File expansionDirectory)
    {
        File[] jars = expansionDirectory.listFiles(((dir, name) -> name.endsWith(".jar")));

        if (jars == null)
            return;

        Map<String, WorldEventExpansion> newExpansions = new HashMap<>();

        for (File expansionFile : jars)
        {
            for (Class<? extends WorldEventExpansion> expansionClass : findClasses(expansionFile, WorldEventExpansion.class))
            {
                WorldEventExpansion expansion = createExpansionInstance(expansionClass);

                if (expansion.getKey() == null || expansion.getKey().isEmpty()) {
                    WorldEvents.getInstance().getLog().error("Can not register expansion in class '" + expansionClass.getName() + "' because key is not specified!");
                    continue;
                }
                if (expansion.getAction() == null) {
                    WorldEvents.getInstance().getLog().error("Can not register expansion in class '" + expansionClass.getName() + "' because action is not specified!");
                    continue;
                }
                if (expansion.getExpansionData() == null) {
                    WorldEvents.getInstance().getLog().error("Can not register expansion in class '" + expansionClass.getName() + "' because expansion data is not specified!");
                    continue;
                }

                if (expansionRegistry.getRegisteredExpansions().containsKey(expansion.getKey()))
                    continue;

                expansion.init(new ExpansionDataProvider(expansion, expansionDirectory));
                newExpansions.put(expansion.getKey(), expansion);
            }
        }

        expansionRegistry.registerExpansions(newExpansions);
    }

    private <T> List<Class<? extends T>> findClasses(File file, Class<T> clazz) {
        if (!file.exists()) {
            return new ArrayList<>();
        }

        URL jar = null;
        try {
            jar = file.toURI().toURL();
        } catch (MalformedURLException e) {
            WorldEvents.getInstance().getLog().error("An exception occurred trying to format file to URL", e);
        }
        final URLClassLoader loader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
        final List<String> matches = new ArrayList<>();
        final List<Class<? extends T>> classes = new ArrayList<>();

        try (final JarInputStream stream = new JarInputStream(jar.openStream())) {
            JarEntry entry;
            while ((entry = stream.getNextJarEntry()) != null) {
                final String name = entry.getName();
                if (entry.isDirectory()
                || !name.endsWith(".class")
                || name.startsWith("META-INF/")) {
                    continue;
                }

                matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
            }

            for (final String match : matches) {
                try {
                    if (cachedClassMatches.contains(match)) continue;
                    final Class<?> loaded = loader.loadClass(match);
                    if (clazz.isAssignableFrom(loaded)) {
                        classes.add(loaded.asSubclass(clazz));
                    } else {
                        cachedClassMatches.add(match);
                    }
                } catch (Throwable ignored) {
                    cachedClassMatches.add(match);
                }
            }

            loader.close();
        } catch (IOException e) {
            WorldEvents.getInstance().getLog().error("An exception occurred trying to find expansion classes", e);
        }

        return classes;
    }

    @Nullable
    private WorldEventExpansion createExpansionInstance(Class<? extends WorldEventExpansion> clazz)
    {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            WorldEvents.getInstance().getLog().error("An exception occurred trying to create '" + clazz.getName() + "' expansion class", e);
        }
        return null;
    }

}
