package listeners;

import entities.Room;
import entities.Server;
import entities.Topic;
import info.BotResponses;
import net.dv8tion.jda.api.EmbedBuilder;
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
         *
         * @param member The member that called the command
         * @param channel The channel that the command was called in
         * @param server The Server that the command was called in
         * @param args Extra command arguments, if any
         */
        void handle(@NotNull Member member, TextChannel channel, Server server, String[] args);
    }

    /**
     * Map from a server name to a Server object.
     */
    private final HashMap<String, Server> servers = new HashMap<>();

    /**
     * Check if the given Member has administrator permissions.
     *
     * @param member The Member to check
     *
     * @return True if the Member has admin, false otherwise
     */
    private static boolean isAdmin(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    /**
     * Check if the given Member is a mentor for a topic.
     *
     * @param member The Member to check
     * @param topic The Topic to check
     *
     * @return True if the Member has permission, false otherwise
     */
    private static boolean isMentor(Member member, Topic topic) {
        return member.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals(topic.getRoleName()));
    }

    /**
     * Check if the given Member is a mentor for any topic.
     *
     * @param member The Member to check
     *
     * @return True if the Member is a mentor, false otherwise
     */
    private static boolean isMentor(Member member) {
        return member.getRoles()
                .stream()
                .anyMatch(r -> r.getName().startsWith(Topic.PREFIX));
    }

    /**
     * Check if the given Topic exists in the Server. Notifies the user if the topic does not exist.
     *
     * @param member The Member to reply to
     * @param channel The Channel to reply within
     * @param server The Server to check in
     * @param topicName The Topic to check for
     *
     * @return Contains the Topic, if it exists
     */
    private static Optional<Topic> checkTopicExists(Member member, TextChannel channel, Server server, String topicName) {
        Optional<Topic> optionalTopic = server.getTopic(topicName.toLowerCase());
        if (optionalTopic.isEmpty()) {
            BotResponses.noSuchTopic(channel, member, topicName);
        }
        return optionalTopic;
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
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        Member member = Objects.requireNonNull(event.getMember());
        TextChannel channel = event.getChannel();

        String guildID = event.getGuild().getId();
        Server server = servers.computeIfAbsent(guildID, k -> new Server(event.getGuild()));

        // pick the correct method to call
        CommandHandler commandHandler;
        switch (tokens[0].substring(1)) {
            case "help"        -> commandHandler = this::help;
            case "maketopic"   -> commandHandler = this::makeTopic;
            case "deletetopic" -> commandHandler = this::deleteTopic;
            case "showtopics"  -> commandHandler = this::showTopics;
            case "queue"       -> commandHandler = this::queue;
            case "ready"       -> commandHandler = this::ready;
            case "showqueue"   -> commandHandler = this::showQueue;
            case "clear"       -> commandHandler = this::clear;
            case "finish"      -> commandHandler = this::finish;
            default            -> commandHandler = this::unknownCommand;
        }
        commandHandler.handle(member, channel, server, args);
    }

    private void help(Member member, TextChannel channel, Server server, String[] args) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Help!");
        embedBuilder.setDescription("Possible commands:");
        embedBuilder.setColor(0xE57D25);

        embedBuilder.addField("$queue <topic>", "Add yourself to a queue.", false);
        embedBuilder.addField("$showqueue <topic>", "Show the people currently in queue.", false);
        embedBuilder.addField("$showtopics", "List all topics.", false);

        if (isMentor(member)) {
            embedBuilder.addField("$ready <topic> (mentor only)", "Retrieve the next person from the queue.", false);
            embedBuilder.addField("$clear <topic> (mentor only)", "Clear the specified queue.", false);
            embedBuilder.addField("$finish (mentor only)", "Finish a mentoring session. Must be run inside the text channel for that session.", false);
        }

        if (isAdmin(member)) {
            embedBuilder.addField("$maketopic <name> (admin only)", "Create a new topic.", false);
            embedBuilder.addField("$deletetopic <name> (admin only)", "Delete a topic.", false);
        }

        channel.sendMessage(embedBuilder.build()).queue();
    }

    private void makeTopic(Member member, TextChannel channel, Server server, String[] args) {
        // do not allow non-admins to run command
        if (!isAdmin(member)) {
            BotResponses.noPermission(channel, member);
            return;
        }

        String topicName = args[0];
        server.createTopic(topicName);

        BotResponses.topicCreated(channel, member, topicName);
    }

    private void deleteTopic(Member member, TextChannel channel, Server server, String[] args) {
        // do not allow non-admins to run command
        if (!isAdmin(member)) {
            BotResponses.noPermission(channel, member);
            return;
        }

        String topicName = args[0];
        server.deleteTopic(topicName);

        BotResponses.topicDeleted(channel, member, topicName);
    }

    private void showTopics(Member member, TextChannel channel, Server server, String[] args) {
        String topicList = Arrays.stream(server.getTopics())
                .map(Topic::getName)
                .collect(Collectors.joining("\n"));

        BotResponses.sendTopicList(channel, member, topicList);
    }

    private void queue(Member member, TextChannel channel, Server server, String[] args) {
        String topicName = args[0];

        // do not run if topic does not exist
        Optional<Topic> optionalTopic = checkTopicExists(member, channel, server, topicName);
        if (optionalTopic.isEmpty()) return;

        Topic topic = optionalTopic.get();
        if (topic.isInQueue(member)) {
            topic.removeFromQueue(member);
            BotResponses.leftQueue(channel, member, topicName);
        } else {
            topic.addToQueue(member);
            BotResponses.joinedQueue(channel, member, topicName);
        }
    }

    private void ready(Member member, TextChannel channel, Server server, String[] args) {
        String topicName = args[0];

        // do not run if topic does not exist
        Optional<Topic> optionalTopic = checkTopicExists(member, channel, server, topicName);
        if (optionalTopic.isEmpty()) return;

        // do not run if caller does not have mentor role for this topic or admin privileges
        Topic topic = optionalTopic.get();
        if (!isMentor(member, topic) && !isAdmin(member)) {
            BotResponses.noPermission(channel, member);
            return;
        }

        Member mentee = topic.popFromQueue();
        Room room = topic.createRoom(mentee);
        BotResponses.mentorIsReady(channel, member, mentee, room);
    }

    private void showQueue(Member member, TextChannel channel, Server server, String[] args) {
        String topicName = args[0];

        // do not run if topic does not exist
        Optional<Topic> optionalTopic = checkTopicExists(member, channel, server, topicName);
        if (optionalTopic.isEmpty()) return;

        Topic topic = optionalTopic.get();

        if (topic.getMembersInQueue().length == 0) {
            BotResponses.queueIsEmpty(channel, member, topic);;
        } else {
            String menteeList = Arrays.stream(topic.getMembersInQueue())
                    .map(Member::getEffectiveName)
                    .collect(Collectors.joining("\n"));

            BotResponses.showQueueMembers(channel, member, topic, menteeList);
        }
    }

    private void clear(Member member, TextChannel channel, Server server, String[] args) {
        String topicName = args[0];

        // do not run if topic does not exist
        Optional<Topic> optionalTopic = checkTopicExists(member, channel, server, topicName);
        if (optionalTopic.isEmpty()) return;

        // do not run if caller does not have mentor role for this topic or admin privileges
        Topic topic = optionalTopic.get();
        if (!isMentor(member, topic) && !isAdmin(member)) {
            BotResponses.noPermission(channel, member);
            return;
        }

        Arrays.stream(topic.getMembersInQueue()).forEach(topic::removeFromQueue);

        BotResponses.queueCleared(channel, member, topic);
    }

    private void finish(Member member, TextChannel channel, Server server, String[] args) {
        Topic topic = null;
        Room room = null;

        // find the topic corresponding to this channel
        for (Topic t : server.getTopics()) {
            Optional<Room> optionalRoom = t.getRoom(channel.getName());
            if (optionalRoom.isPresent()) {
                topic = t;
                room = optionalRoom.get();
            }
        }

        // only run inside a room
        if (room == null) {
            BotResponses.runInTopicChannel(channel, member);
            return;
        }

        // do not run if caller does not have mentor role for this topic or admin privileges
        if (!isMentor(member, topic) && !isAdmin(member)) {
            BotResponses.noPermission(channel, member);
            return;
        }

        topic.deleteRoom(room);
    }

    private void unknownCommand(Member member, TextChannel channel, Server server, String[] args) {
        BotResponses.noSuchCommand(channel, member);
    }
}
