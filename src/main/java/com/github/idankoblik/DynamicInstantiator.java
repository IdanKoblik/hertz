package com.github.idankoblik;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utility class for dynamically instantiating classes from a given package.
 */
@SuppressWarnings("unused")
public class DynamicInstantiator {

    private final ClassLoader loader;

    /**
     * Constructs a DynamicInstantiator with the default class loader.
     */
    public DynamicInstantiator() {
        this.loader = getClass().getClassLoader();
    }

    /**
     * Registers classes from a specified package that extend or implement a given base class,
     * and performs a specified action on each instantiated object.
     *
     * @param packageName The name of the package to scan for classes.
     * @param baseClass   The base class/interface that the registered classes should extend or implement.
     * @param action      The action to perform on each instantiated object.
     * @param <T>         The type of the base class/interface.
     */
    public <T> void registerClasses(String packageName, Class<T> baseClass, Consumer<T> action) {
        for (ClassPath.ClassInfo classInfo : getClassInfos(packageName)) {
            try {
                Class<?> clazz = Class.forName(classInfo.getName(), true, loader);
                if (baseClass.isAssignableFrom(clazz) && !clazz.equals(baseClass))
                    action.accept(clazz.asSubclass(baseClass).getDeclaredConstructor().newInstance());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Retrieves all class infos from a specified package.
     *
     * @param packageName The name of the package to retrieve class infos from.
     * @return An ImmutableSet of ClassPath.ClassInfo objects representing the classes in the package.
     */
    public ImmutableSet<ClassPath.ClassInfo> getClassInfos(String packageName) {
        ClassPath classPath;
        try {
            classPath = ClassPath.from(loader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classPath.getTopLevelClassesRecursive(packageName);
    }

    /**
     * Retrieves the class loader associated with this DynamicInstantiator.
     *
     * @return The class loader.
     */
    public ClassLoader getLoader() {
        return loader;
    }

}
