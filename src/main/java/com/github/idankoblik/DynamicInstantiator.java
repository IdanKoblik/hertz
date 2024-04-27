package com.github.idankoblik;

import com.google.common.reflect.ClassPath;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Consumer;

public class DynamicInstantiator {

    public <T> void registerClasses(String packageName, Class<T> baseClass, Consumer<T> action) {
        ClassLoader classLoader = getClass().getClassLoader();

        ClassPath classPath;
        try {
            classPath = ClassPath.from(classLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(packageName)) {
            try {
                Class<?> clazz = Class.forName(classInfo.getName(), true, classLoader);
                if (baseClass.isAssignableFrom(clazz) && !clazz.equals(baseClass))
                    action.accept(clazz.asSubclass(baseClass).getDeclaredConstructor().newInstance());
            } catch (Throwable e) {
               throw new RuntimeException(e);
            }
        }
    }

    public <T extends Annotation> Optional<T> getAnnotation(String packageName, Class<T> annotation) {
        ClassLoader classLoader = getClass().getClassLoader();

        ClassPath classPath;
        try {
            classPath = ClassPath.from(classLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(packageName)) {
            try {
                Class<?> clazz = Class.forName(classInfo.getName(), true, classLoader);
                if (clazz.isAnnotationPresent(annotation))
                    return Optional.ofNullable(clazz.getAnnotation(annotation));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.empty();
    }
}
