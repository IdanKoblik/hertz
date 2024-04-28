package com.github.idankoblik.exceptions;

/**
 * Triggered when given discord guild not found
 */
public class GuildNotFoundException extends RuntimeException {

    /**
     *
     * @param message the error message
     */
    public GuildNotFoundException(String message) {
        super(message);
    }

}
