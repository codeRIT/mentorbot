package entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

public class Room {
    /**
     * The Category that this room's channels exist within
     */
    private final Category category;

    /**
     * The name of this room
     */
    private final String name;

    /**
     * The text channel for this room
     */
    private final TextChannel textChannel;

    /**
     * The voice channel for this room
     */
    private final VoiceChannel voiceChannel;

    /**
     * Create a new room. Existing channels for this room number (e.g. from
     * before a bot restart) will be deleted automatically.
     * @param topic The Topic for this room
     * @param number This room's number
     * @param mentee The mentee using this room
     */
    public Room(Topic topic, int number, Member mentee) {
        this.category = topic.getCategory();
        this.name = makeName(topic.getName(), number);

        deleteExisting();

        textChannel = category.createTextChannel(name).complete();
        Guild guild = textChannel.getGuild();
        ArrayList<IPermissionHolder> allowList = new ArrayList<IPermissionHolder>() {
            // makes the linter stop complaining about not having a version UID
            private static final long serialVersionUID = 1L;

            {
                add(guild.getMember(guild.getJDA().getSelfUser()));  // allow the bot itself
                add(topic.getRole());  // allow this topics' mentors
                add(mentee);  // allow the mentee
            }
        };
        setChannelPermissions(textChannel, guild.getPublicRole(), allowList);

        voiceChannel = category.createVoiceChannel(name).complete();
        setChannelPermissions(voiceChannel, guild.getPublicRole(), allowList);
    }

    /**
     * Create a Room name from a Topic name and room number.
     * @param topicName The name of the owning Topic
     * @param roomNumber The room's number
     * @return The Room's name
     */
    public static String makeName(String topicName, int roomNumber) {
        return String.format("%s-%d", topicName, roomNumber);
    }

    /**
     * Deny view permissions to `veryoneRole` and allow view permissions to all
     * roles/members in `allowList`.
     * @param channel The channel to apply permission overrides to 
     * @param everyoneRole Reference to `@everyone` role
     * @param allowList List of roles/members that should have access to this
     *     channel. The bot's user MUST be in this list.
     */
    private void setChannelPermissions(GuildChannel channel, IPermissionHolder everyoneRole, Collection<IPermissionHolder> allowList) {
        for (IPermissionHolder holder : allowList) {
            channel.putPermissionOverride(holder)
                .setAllow(Permission.VIEW_CHANNEL)
                .complete();
        }

        channel.putPermissionOverride(everyoneRole)
            .setDeny(Permission.VIEW_CHANNEL)
            .complete();
    }

    /**
     * Delete this room's channels from the server. This object should be disposed
     * of after calling this method.
     */
    public void delete() {
        textChannel.delete().complete();
        voiceChannel.delete().complete();
    }

    /**
     * Delete any existing channels using this name/number
     */
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

    /**
     * Create an invite to this room's voice channel. This invite can be used
     * twice and expires after 5 minutes.
     * @return A new Invite for this room's voice channel
     */
    public Invite getVoiceChannelInvite() {
        InviteAction action = voiceChannel.createInvite();
        action.setMaxAge(5 * 60);  // 5 minutes, to prevent hitting the invite cap
        action.setMaxUses(2);
        return action.complete();
    }

    /**
     * Get this room's text channel
     * @return This room's text channel
     */
    public TextChannel getTextChannel() {
        return this.textChannel;
    }

    /**
     * Get this room's voice channel
     * @return This room's voice channel
     */
    public VoiceChannel getVoiceChannel() {
        return this.voiceChannel;
    }

    /**
     * Get this room's name
     * @return This room's name
     */
    public String getName() {
        return name;
    }
}
