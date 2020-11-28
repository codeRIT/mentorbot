package entities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that provides methods to store Topic information into
 * a Guild's role list.
 */
public class Server {
    public static final String TOPIC_PREFIX = "Topic | ";

    private final Guild guild;
    private final ArrayList<Topic> topics = new ArrayList<>();

    /**
     * Constructs a Server object from a Guild's role list.
     * @param guild The Guild that this object is associated with
     */
    public Server(Guild guild) {
        this.guild = guild;
        List<Role> roles = guild.getRoles();
        for (Role role : roles) {
            String name = role.getName();
            if (name.startsWith(TOPIC_PREFIX)) {
                topics.add(new Topic(name.substring(8)));
            }
        }
    }

    @Override
    public int hashCode() {
        return guild.getName().hashCode();
    }

    /**
     * Creates a new Topic role in this server.
     * @param topic The Topic to add
     */
    public void createTopic(Topic topic) {
        guild.createRole()
                .setName(TOPIC_PREFIX + topic.getName())
                .setMentionable(true)
                .queue();
        topics.add(topic);
    }

    /**
     * Deletes the Topic role from this server.
     * @param topic The Topic to remove
     */
    public void deleteTopic(Topic topic) {
        guild.getRolesByName(TOPIC_PREFIX + topic.getName(), true)
                .forEach((role -> role.delete().queue()));
        topics.remove(topic);
    }

    /**
     * Gets all Topics from this Server
     * @return An array of Topics
     */
    public Topic[] getTopics() {
        /*
         The Java runtime will automatically expand the passed-in array to fit the
         contents of `topics` in a performant, thread-safe manner.
         */
        return topics.toArray(new Topic[0]);
    }
}
