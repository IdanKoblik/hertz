package com.github.idankoblik;

import com.google.common.reflect.ClassPath;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

@SuppressWarnings("unused")
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

    public <T extends Annotation> void processAnnotatedClasses(String packageName, Class<T> annotationClass, Consumer<Class<?>> action) {
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
                if (clazz.isAnnotationPresent(annotationClass)) {
                    action.accept(clazz);
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

}
