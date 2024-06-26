package com.github.idankoblik.commands;

import com.github.idankoblik.DynamicInstantiator;
import com.github.idankoblik.exceptions.GuildNotFoundException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.*;
import java.util.stream.Stream;

/**
 * Auto registers JDA slash commands
 */
@SuppressWarnings("unused")
public class ReflactiveCommandLoader {

    private final Map<String, Command> commands = new HashMap<>();

    private final String packageName;

    private final DynamicInstantiator instantiator;

    /**
     * Constructs a ReflactiveCommandLoader for handling dynamic commands.
     *
     * @param jda         The JDA instance.
     * @param guildId     The ID of the guild.
     * @param packageName The name of the package containing command classes.
     * @throws GuildNotFoundException If the specified guild ID does not exist.
     */
    public ReflactiveCommandLoader(JDA jda, long guildId, String packageName) {
        this.packageName = packageName;

        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
            throw new GuildNotFoundException(String.format("Guild: %d not found", guildId));

        this.instantiator = new DynamicInstantiator();
        instantiator.registerClasses(packageName, Command.class, this::addCommand);

        guild.updateCommands().addCommands(commands.values().stream().map(Command::commandData).toList()).queue();
    }

    /**
     * Handles slash command interactions.
     *
     * @param event The SlashCommandInteractionEvent.
     */
    public void handleCommands(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null)
            return;

        Command command = commands.get(event.getName().toLowerCase());
        if (command == null)
            return;

        long[] requiredRoles = getRequiredRoles(command);
        if (requiredRoles == null) {
            command.execute(event);
            return;
        }

        if (Arrays.stream(requiredRoles).anyMatch(roleID -> hasRole(member, roleID)))
            command.execute(event);
        else
            event.reply("You don't have the required role to use this command").setEphemeral(true).queue();
    }

    /**
     * Handles auto-completion interactions for commands.
     *
     * @param event The CommandAutoCompleteInteractionEvent.
     */
    public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
        Command command = commands.get(event.getName().toLowerCase());
        if (command == null)
            return;

        Map<String, String[]> options = command.autoCompletion();
        if (options == null)
            return;

        for (String option : options.keySet()) {
            if (event.getFocusedOption().getName().equals(option)) {
                String[] words = options.get(option);
                List<net.dv8tion.jda.api.interactions.commands.Command.Choice> choices = Stream.of(words)
                        .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                        .map(word -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(word, word))
                        .toList();

                event.replyChoices(choices).queue();
            }
        }
    }

    private void addCommand(Command command) {
        this.commands.put(command.commandData().getName().toLowerCase(), command);
    }

    private boolean hasRole(Member member, long id) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                .anyMatch(role -> role.getIdLong() == id);
    }

    private long[] getRequiredRoles(Command command) {
        Optional<RequiredRoles> requiredRolesOptional = Optional.ofNullable(command.getClass().getAnnotation(RequiredRoles.class));
        return requiredRolesOptional.map(RequiredRoles::ids).orElse(null);
    }
}
