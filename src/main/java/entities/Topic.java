package entities;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

import java.util.LinkedList;
import java.util.Optional;

/**
 * A topic for a server. Internally contains a queue of Members.
 */
public class Topic {
    public class Channels {
        private final TextChannel textChannel;
        private final VoiceChannel voiceChannel;

        public Channels() {
            deleteExisting();

            textChannel = category.createTextChannel(name).complete();
            textChannel.putPermissionOverride(mentee).complete();

            voiceChannel = category.createVoiceChannel(name).complete();
            voiceChannel.putPermissionOverride(mentee).complete();
        }

        public void delete() {
            textChannel.delete().complete();
            voiceChannel.delete().complete();
        }

        public void deleteExisting() {
            Optional<TextChannel> optionalTextChannel = category.getTextChannels().stream()
                .filter(tc -> tc.getName().equals(name.toLowerCase()))  // text channels are lowercase
                .findFirst();
            optionalTextChannel.ifPresent(tc -> tc.delete().complete());

            Optional<VoiceChannel> optionalVoiceChannel = category.getVoiceChannels().stream()
                .filter(vc -> vc.getName().equals(name))
                .findFirst();
            optionalVoiceChannel.ifPresent(vc -> vc.delete().complete());
        }

        public Invite getVoiceChannelInvite() {
            InviteAction action = voiceChannel.createInvite();
            action.setMaxAge(5 * 60);  // 5 minutes, to prevent hitting the invite cap
            action.setMaxUses(2);
            return action.complete();
        }

        public TextChannel getTextChannel() {
            return this.textChannel;
        }

        public VoiceChannel getVoiceChannel() {
            return this.voiceChannel;
        }
    }

    private final String name;
    private final Role role;
    private final Category category;
    private final LinkedList<Member> queue = new LinkedList<>();

    private Member mentee = null;
    private Channels channels = null;

    /**
     * Constructs a new Topic object. This does not automatically create
     * the topic on the Discord server.
     * @param name The name of the topic.
     * @param role The Role that represents this in the guild
     * @param category The Category that this topic's channels should be
     *                 added to
     */
    public Topic(String name, Role role, Category category) {
        this.name = name;
        this.role = role;
        this.category = category;
    }

    /**
     * Add a Member to the back of the queue.
     * @param member The Member to add
     */
    public void addToQueue(Member member) {
        queue.add(member);
    }

    /**
     * Remove a Member from their position in the queue.
     * @param member The Member to remove
     */
    public void removeFromQueue(Member member) {
        queue.remove(member);
    }

    /**
     * Check if a Member is inside the queue.
     * @param member The Member to check
     * @return True if the member is in the queue, false otherwise
     */
    public boolean isInQueue(Member member) {
        return queue.contains(member);
    }

    /**
     * Returns an array of all Members in the queue.
     * @return The Members in this queue
     */
    public Member[] getMembersInQueue() {
        return queue.toArray(new Member[0]);
    }

    /**
     * Remove and return the next Member in the queue. This also
     * automatically sets `mentee`.
     * @return The new mentee
     */
    public Member getNextFromQueue() {
        mentee = queue.remove();
        return mentee;
    }

    /**
     * Creates a new text channel for this Topic and adds the
     * current mentee as an authorized user. If a channel already
     * exists, it will be deleted.
     */
    public Channels setupChannels() {
        deleteChannels();
        channels = new Channels();
        return channels;
    }

    /**
     * Deletes this topic's text channel, if it exists.
     */
    public void deleteChannels() {
        if (channels != null) {
            channels.delete();
            channels = null;
        }
    }

    /**
     * Get the name of this Topic.
     * @return This Topic's name
     */
    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }
}
