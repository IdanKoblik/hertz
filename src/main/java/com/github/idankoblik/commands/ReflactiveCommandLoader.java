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

public class ReflactiveCommandLoader {

    private final Map<String, Command> commands = new HashMap<>();

    private final String packageName;

    private final DynamicInstantiator instantiator;

    public ReflactiveCommandLoader(JDA jda, long guildId, String packageName) {
        this.packageName = packageName;

        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
            throw new GuildNotFoundException(String.format("Guild: %d not found", guildId));

        this.instantiator = new DynamicInstantiator();
        instantiator.registerClasses(packageName, Command.class, this::addCommand);

        guild.updateCommands().addCommands(commands.values().stream().map(Command::commandData).toList()).queue();
    }

    public void handleCommands(SlashCommandInteractionEvent event) {
        Command command = commands.get(event.getName().toLowerCase());
        if (command == null) return;

        long[] requiredRoles = getRequiredRoles(packageName);
        if (requiredRoles == null)
            return;

        if (requiredRoles.length == 0)
            return;

        Member member = event.getMember();
        if (member == null)
            return;

        if (Arrays.stream(requiredRoles).anyMatch(roleID -> hasRole(member, roleID)))
            command.execute(event);
    }

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

    private long[] getRequiredRoles(String packageName) {
        Optional<RequiredRoles> requiredRolesOptional = instantiator.getAnnotation(packageName, RequiredRoles.class);
        return requiredRolesOptional.map(RequiredRoles::ids).orElse(null);
    }
}
