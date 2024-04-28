package com.github.idankoblik;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class DynamicInstantiator {

    private final ClassLoader loader;

    public DynamicInstantiator() {
        this.loader = getClass().getClassLoader();
    }

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

    public ImmutableSet<ClassPath.ClassInfo> getClassInfos(String packageName) {
        ClassPath classPath;
        try {
            classPath = ClassPath.from(loader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classPath.getTopLevelClassesRecursive(packageName);
    }

    public ClassLoader getLoader() {
        return loader;
    }

}
