package com.github.idankoblik.listeners;

import com.github.idankoblik.DynamicInstantiator;
import com.google.common.reflect.ClassPath;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@SuppressWarnings("unused")
public class ReflactiveListenerLoader {

    private final DynamicInstantiator instantiator;
    private final JDABuilder builder;

    public ReflactiveListenerLoader(JDABuilder builder) {
        this.instantiator = new DynamicInstantiator();
        this.builder = builder;
    }

    public void registerListener(String packageName) {
        for (ClassPath.ClassInfo classInfo : instantiator.getClassInfos(packageName)) {
            try {
                Class<?> clazz = Class.forName(classInfo.getName(), true, instantiator.getLoader());
                if (ListenerAdapter.class.isAssignableFrom(clazz))
                    builder.addEventListeners(clazz.getDeclaredConstructor().newInstance());
            } catch (Throwable e) {
                e.getStackTrace();
            }
        }
    }

}
