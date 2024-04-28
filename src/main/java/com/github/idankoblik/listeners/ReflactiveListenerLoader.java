package com.github.idankoblik.listeners;

import com.github.idankoblik.DynamicInstantiator;
import net.dv8tion.jda.api.JDA;

public class ReflactiveListenerLoader {

    public ReflactiveListenerLoader(JDA jda, String packageName) {
        DynamicInstantiator instantiator = new DynamicInstantiator();
        instantiator.processAnnotatedClasses(packageName, Listener.class, listener -> addCommand(listener, jda));
    }

    private void addCommand(Class<?> listener, JDA jda) {
        Object instance;
        try {
            instance = listener.getDeclaredConstructors()[0].newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        jda.addEventListener(instance);
    }

}
