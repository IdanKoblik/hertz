package com.github.idankoblik.listeners;

import com.github.idankoblik.DynamicInstantiator;
import com.google.common.reflect.ClassPath;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@SuppressWarnings("unused")
public class ReflactiveListenerLoader {

    private final DynamicInstantiator instantiator;
    private final JDA jda;

    public ReflactiveListenerLoader(JDA jda) {
        this.instantiator = new DynamicInstantiator();
        this.jda = jda;
    }

    public void registerListener(String packageName) {
        for (ClassPath.ClassInfo classInfo : instantiator.getClassInfos(packageName)) {
            try {
                Class<?> clazz = Class.forName(classInfo.getName(), true, instantiator.getLoader());
                if (ListenerAdapter.class.isAssignableFrom(clazz))
                    jda.addEventListener(clazz.getDeclaredConstructor().newInstance());
            } catch (Throwable e) {
                e.getStackTrace();
            }
        }
    }

}
