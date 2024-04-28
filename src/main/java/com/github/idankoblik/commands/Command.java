package com.github.idankoblik.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Abstract class representing a command.
 */
public abstract class Command {

    /**
     * Executes the command in response to a slash command interaction event.
     *
     * @param event The SlashCommandInteractionEvent representing the interaction event.
     */
    public abstract void execute(SlashCommandInteractionEvent event);

    /**
     * Retrieves the command data associated with this command.
     *
     * @return The CommandData representing the command.
     */
    @NonNull
    public abstract CommandData commandData();

    /**
     * Retrieves auto-completion options for the command.
     *
     * @return A Map containing auto-completion options, where the key is the option name and the value is an array of possible completions.
     *         Returns null if auto-completion is not supported for this command.
     */
    @Nullable
    public abstract Map<String, String[]> autoCompletion();

}

