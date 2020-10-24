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
     * Check if the given Member has administrator permissions. Notifies the user if they do not have permission.
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

    /**
     * Check if the given Topic exists in the Server. Notifies the user if the topic does not exist.
     * @param member The Member to reply to
     * @param channel The Channel to reply within
     * @param server The Server to check in
     * @param topic The Topic to check for
     * @return Contains the Topic, if it exists
     */
    private static Optional<Topic> checkTopicExists(Member member, TextChannel channel, Server server, String topic) {
        Optional<Topic> topicOptional = Arrays.stream(server.getTopics())
                .filter(t -> t.getName().equalsIgnoreCase(topic))
                .findFirst();
        if (topicOptional.isEmpty()) {
            channel.sendMessage(String.format(
                    "%s Topic \"%s\" does not exist.",
                    member.getAsMention(),
                    topic)).complete();
        }
        return topicOptional;
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
            case "queue" -> commandHandler = this::queue;
            case "ready" -> commandHandler = this::ready;
            case "showqueue" -> commandHandler = this::showqueue;
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

    private void queue(Member member, TextChannel channel, Server server, String[] args) {
        Optional<Topic> topicOptional = checkTopicExists(member, channel, server, args[0]);
        if (topicOptional.isEmpty()) return;

        Topic topic = topicOptional.get();
        if (topic.isInQueue(member)) {
            topic.removeFromQueue(member);
            channel.sendMessage(String.format(
                    "%s has left the \"%s\" queue.",
                    member.getAsMention(),
                    args[0])).complete();
        } else {
            topic.addToQueue(member);
            channel.sendMessage(String.format(
                    "%s has joined the \"%s\" queue.",
                    member.getAsMention(),
                    args[0])).complete();
        }
    }

    private void ready(Member member, TextChannel channel, Server server, String[] args) {
        if (!checkAdmin(member, channel)) return;

        Optional<Topic> topicOptional = checkTopicExists(member, channel, server, args[0]);
        if (topicOptional.isEmpty()) return;

        Topic topic = topicOptional.get();
        Member mentee = topic.getNextFromQueue();
        channel.sendMessage(String.format(
                "%s is ready for %s.",
                member.getAsMention(),
                mentee.getAsMention())).complete();
    }

    private void showqueue(Member member, TextChannel channel, Server server, String[] args) {
        Optional<Topic> topicOptional = checkTopicExists(member, channel, server, args[0]);
        if (topicOptional.isEmpty()) return;

        Topic topic = topicOptional.get();
        if (topic.getMembersInQueue().length == 0) {
            channel.sendMessage(String.format(
                    "%s Queue \"%s\" is empty.",
                    member.getAsMention(),
                    topic.getName())).complete();
        } else {
            String menteeList = Arrays.stream(topic.getMembersInQueue())
                    .map(Member::getEffectiveName)
                    .collect(Collectors.joining("\n"));

            channel.sendMessage(String.format(
                    "%s Members in \"%s\" queue:\n%s",
                    member.getAsMention(),
                    topic.getName(),
                    menteeList)).complete();
        }
    }

    private void unknownCommand(Member member, TextChannel channel, Server server, String[] args) {
        channel.sendMessage(member.getAsMention() + " Command does not exist! Try $help for a list of valid commands.").complete();
    }
}
