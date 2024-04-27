package com.github.idankoblik.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class Command {

    public abstract void execute(SlashCommandInteractionEvent event);

    @NonNull
    public abstract CommandData commandData();

    @Nullable
    public abstract Map<String, String[]> autoCompletion();

}
