package entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import info.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

/**
 * A logical group for a topic's text and voice channels. All mentor-mentee
 * interactions occur within a mentoring room.
 */
public class Room {
    private final Category category;
    private final String name;
    private final TextChannel textChannel;
    private final VoiceChannel voiceChannel;

    private static int nextRoomNumber = 1;

    /**
     * Create a new room. Existing channels for this room number (e.g. from
     * before a bot restart) will be deleted automatically.
     *
     * @param topic The Topic for this room
     * @param number This room's number
     * @param mentee The mentee using this room
     */
    public Room(Topic topic, Member mentee) {
        this.category = topic.getCategory();
        this.name = String.format("%s-%d", topic.getName(), nextRoomNumber);
        nextRoomNumber++;

        deleteExisting();

        textChannel = category.createTextChannel(name).complete();
        Guild guild = textChannel.getGuild();

        ArrayList<IPermissionHolder> allowList = new ArrayList<IPermissionHolder>();
        allowList.add(guild.getMember(guild.getJDA().getSelfUser()));  // allow the bot itself
        allowList.add(topic.getRole());  // allow this topics' mentors
        allowList.add(mentee);  // allow the mentee

        // allow all admin roles
        for (String adminRoleName : Config.ADMIN_ROLES) {
            for (Role adminRole : guild.getRolesByName(adminRoleName, false)) {
                allowList.add(adminRole);
            }
        }

        setChannelPermissions(textChannel, guild.getPublicRole(), allowList);

        voiceChannel = category.createVoiceChannel(name).complete();
        setChannelPermissions(voiceChannel, guild.getPublicRole(), allowList);
    }

    /**
     * Deny view permissions to `everyoneRole` and allow view permissions to
     * all roles/members in `allowList`.
     *
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
     *
     * @return A new Invite for this room's voice channel
     */
    public Invite getVoiceChannelInvite() {
        InviteAction action = voiceChannel.createInvite();
        action.setMaxAge(5 * 60);  // 5 minutes, to prevent hitting the invite cap
        action.setMaxUses(5);
        return action.complete();
    }

    /**
     * Get this room's text channel
     *
     * @return This room's text channel
     */
    public TextChannel getTextChannel() {
        return this.textChannel;
    }

    /**
     * Get this room's voice channel
     *
     * @return This room's voice channel
     */
    public VoiceChannel getVoiceChannel() {
        return this.voiceChannel;
    }

    /**
     * Get this room's name
     *
     * @return This room's name
     */
    public String getName() {
        return name;
    }
}
