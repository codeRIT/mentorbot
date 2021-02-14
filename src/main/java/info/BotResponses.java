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
     * Telling the user they do not have command permission
     *
     * @param channel The text channel to send message to
     * @param member The member to ping/mention
     */
    public static void noPermission(TextChannel channel, Member member) {
        channel.sendMessage(member.getAsMention() + " You do not have permission to run this command.").queue();
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

    public static void mentorIsReady(TextChannel channel, Member member, Member mentee, Room room) {
        channel.sendMessage(String.format(
            "%s is ready for %s.\n\nText channel: %s\nVoice channel: %s",
            member.getAsMention(),
            mentee.getAsMention(),
            room.getTextChannel().getAsMention(),
            room.getVoiceChannelInvite().getUrl())).queue();
    }
}
