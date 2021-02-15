package info;

import entities.Room;
import entities.Topic;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Responses that the bot gives the user
 */
public class BotResponses {
    /**
     * Telling the user they do not have permission for a command
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     */
    public static void noPermission(TextChannel channel, Member member) {
        channel.sendMessage(member.getAsMention() + " You do not have permission to run this command.").queue();
    }

    /**
     * Telling the user they do not have admin permission for a command
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     */
    public static void noAdminPermission(TextChannel channel, Member member) {
        channel.sendMessage(member.getAsMention() + " You must have administrator permission to run this command.").queue();
    }

    /**
     * Telling the user that the attempted command does not exist
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     */
    public static void noSuchCommand(TextChannel channel, Member member) {
        channel.sendMessage(member.getAsMention() + " Command does not exist! Try $help for a list of valid commands.").queue();
    }

    /**
     * Telling the user that the attempted command must be run in a topic's text channel
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     */
    public static void runInTopicChannel(TextChannel channel, Member member) {
        channel.sendMessage(String.format(
            "%s This command must be run inside a topic's text channel.",
            member.getAsMention())).queue();
    }

    /**
     * Confirmation feedback for clearing the queue
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topic The topic queue that has been cleared
     */
    public static void queueCleared(TextChannel channel, Member member, Topic topic) {
        channel.sendMessage(String.format(
                "%s has cleared the \"%s\" queue.",
                member.getAsMention(),
                topic.getName())).queue();
    }

    /**
     * Showing the list of mentees in a queue
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topic The topic queue that the mentees are in
     * @param menteeList The list of mentees in queue
     */
    public static void showQueueMembers(TextChannel channel, Member member, Topic topic, String menteeList) {
        channel.sendMessage(String.format(
                    "%s Members in \"%s\" queue:\n%s",
                    member.getAsMention(),
                    topic.getName(),
                    menteeList)).queue();
    }

    /**
     * Reporting that the queue is empty
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topic The topic queue that is empty
     */
    public static void queueIsEmpty(TextChannel channel, Member member, Topic topic) {
        channel.sendMessage(String.format(
                    "%s Queue \"%s\" is empty.",
                    member.getAsMention(),
                    topic.getName())).queue();
    }

    /**
     * Letting the mentee know that their mentor is ready
     *
     * @param channel The text channel to send message to
     * @param member The mentor who is ready
     * @param mentee The mentee to be mentored
     * @param room The room that referncing will take place in
     */
    public static void mentorIsReady(TextChannel channel, Member member, Member mentee, Room room) {
        channel.sendMessage(String.format(
            "%s is ready for %s.\n\nText channel: %s\nVoice channel: %s",
            member.getAsMention(),
            mentee.getAsMention(),
            room.getTextChannel().getAsMention(),
            room.getVoiceChannelInvite().getUrl())).queue();
    }

    /**
     * Notifing the user that they have successfully joined the queue
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topicName The topic queue which they have joined
     */
    public static void joinedQueue(TextChannel channel, Member member, String topicName) {
        channel.sendMessage(String.format(
                    "%s has joined the \"%s\" queue.",
                    member.getAsMention(),
                    topicName)).queue();
    }

    /**
     * Notifing the user that they have successfully left the queue
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topicName The topic queue which they have left
     */
    public static void leftQueue(TextChannel channel, Member member, String topicName) {
        channel.sendMessage(String.format(
                    "%s has left the \"%s\" queue.",
                    member.getAsMention(),
                    topicName)).queue();
    }

    /**
     * Showing the user a list of topics for mentoring
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topicList The list of mentoring topics
     */
    public static void sendTopicList(TextChannel channel, Member member, String topicList) {
        channel.sendMessage(String.format(
                "%s List of topics:\n%s",
                member.getAsMention(),
                topicList)).queue();
    }

    /**
     * Confirmation feedback for deleting a topic
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topicName The topic that was deleted
     */
    public static void topicDeleted(TextChannel channel, Member member, String topicName) {
        channel.sendMessage(String.format(
                "%s Topic role \"%s\" has been deleted.",
                member.getAsMention(),
                topicName)).queue();
    }

    /**
     * Confirmation feedback for creating a topic
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     * @param topicName The topic that was created
     */
    public static void topicCreated(TextChannel channel, Member member, String topicName) {
        channel.sendMessage(String.format(
                "%s Topic role \"%s\" has been created.",
                member.getAsMention(),
                topicName)).queue();
    }

    public static void noSuchTopic(TextChannel channel, Member member, String topicName) {
        channel.sendMessage(String.format(
                    "%s Topic \"%s\" does not exist.",
                    member.getAsMention(),
                    topicName)).queue();
    }
}
