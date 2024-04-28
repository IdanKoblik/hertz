package com.github.idankoblik.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the required roles for accessing a command.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRoles {

    /**
     * Specifies the IDs of the roles required to access the annotated command.
     *
     * @return An array of long values representing the IDs of the required roles.
     */
    long[] ids();
}