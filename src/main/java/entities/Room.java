package entities;

import java.util.Optional;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

public class Room {
    private final Category category;
    private final String name;
    private final int number;
    private final Member mentee;

    private final TextChannel textChannel;
    private final VoiceChannel voiceChannel;

    public Room(Topic topic, int number, Member mentee) {
        this.category = topic.getCategory();
        this.name = String.format("%s-%d", topic.getName(), number);
        this.number = number;
        this.mentee = mentee;

        deleteExisting();

        // TODO: restrict permissions
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

    public Member getMentee() {
        return this.mentee;
    }

    public TextChannel getTextChannel() {
        return this.textChannel;
    }

    public VoiceChannel getVoiceChannel() {
        return this.voiceChannel;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}
