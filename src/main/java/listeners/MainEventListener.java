package listeners;

import entities.Server;
import entities.Topic;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class MainEventListener extends ListenerAdapter {
    /**
     * A function that handles a single command.
     */
    private interface CommandHandler {
        /**
         * Handle a single command.
         * @param member The member that called the command
         * @param channel The channel that the command was called in
         * @param server The Server that the command was called in
         * @param args Extra command arguments, if any
         */
        void handle(@NotNull Member member, TextChannel channel, Server server, String[] args);
    }

    private final HashMap<String, Server> servers = new HashMap<>();

    /**
     * Check if the given Member has administrator permissions.
     * @param member The Member to check
     * @return True if the Member has admin, false otherwise
     */
    private static boolean checkAdmin(Member member, TextChannel channel) {
        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            channel.sendMessage(member.getAsMention() + " You must have administrator permission to run this command.").complete();
            return false;
        }
        return true;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Logged in");
        event.getJDA().getPresence().setPresence(Activity.playing("$help"), false);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] tokens = event.getMessage().getContentDisplay().split(" ");
        if (!tokens[0].startsWith("$")) return;

        String guildID = event.getGuild().getId();
        Server server = servers.computeIfAbsent(guildID, k -> Server.load(event.getGuild()));

        // pick the correct method to call
        CommandHandler commandHandler;
        switch (tokens[0].substring(1)) {
            case "help" -> commandHandler = this::help;
            case "maketopic" -> commandHandler = this::maketopic;
            case "deletetopic" -> commandHandler = this::deletetopic;
            case "showtopics" -> commandHandler = this::showtopics;
            default -> commandHandler = this::unknownCommand;
        }
        commandHandler.handle(
                Objects.requireNonNull(event.getMember()),
                event.getChannel(),
                server,
                Arrays.copyOfRange(tokens, 1, tokens.length));
    }

    private void help(Member member, TextChannel channel, Server server, String[] args) {
        channel.sendMessage(member.getAsMention() + " No commands implemented yet!").complete();
    }

    private void maketopic(Member member, TextChannel channel, Server server, String[] args) {
        if (!checkAdmin(member, channel)) return;

        Topic topic = new Topic(args[0]);
        server.createTopic(topic);

        channel.sendMessage(String.format(
                "%s Topic role \"%s\" has been created.",
                member.getAsMention(),
                args[0])).complete();
    }

    private void deletetopic(Member member, TextChannel channel, Server server, String[] args) {
        if (!checkAdmin(member, channel)) return;

        Arrays.stream(server.getTopics())                   // loop over all topics...
                .filter(t -> t.getName().equals(args[0]))   // ...if this topic's name is args[0]...
                .findFirst()
                .ifPresent(server::deleteTopic);            // ...delete it

        channel.sendMessage(String.format(
                "%s Topic role \"%s\" has been deleted.",
                member.getAsMention(),
                args[0])).complete();
    }

    private void showtopics(Member member, TextChannel channel, Server server, String[] args) {
        String topicList = Arrays.stream(server.getTopics())
                .map(Topic::getName)
                .collect(Collectors.joining("\n"));

        channel.sendMessage(String.format(
                "%s List of topics:\n%s",
                member.getAsMention(),
                topicList)).complete();
    }

    private void unknownCommand(Member member, TextChannel channel, Server server, String[] args) {
        channel.sendMessage(member.getAsMention() + " Command does not exist! Try $help for a list of valid commands.").complete();
    }
}
