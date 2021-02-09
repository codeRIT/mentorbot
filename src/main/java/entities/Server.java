package entities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import javax.swing.text.html.Option;
import java.util.*;

/**
 * An object that provides methods to store Topic information into
 * a Guild's role list.
 */
public class Server {
    private final Guild guild;
    private final HashMap<String, Topic> topics = new HashMap<>();

    /**
     * Constructs a Server object from a Guild's role list.
     * @param guild The Guild that this object is associated with
     */
    public Server(Guild guild) {
        this.guild = guild;
        List<Role> roles = guild.getRoles();
        for (Role role : roles) {
            String name = role.getName();
            if (name.startsWith(Topic.PREFIX)) {
                topics.put(
                    name.substring(Topic.PREFIX.length()),
                    new Topic(name.substring(Topic.PREFIX.length()), role)
                );
            }
        }
    }

    @Override
    public int hashCode() {
        return guild.getName().hashCode();
    }

    /**
     * Creates a new Topic role in this server.
     * @param topicName The name for the new topic
     */
    public void createTopic(String topicName) {
        guild.createRole()
                .setName(Topic.PREFIX + topicName)
                .setMentionable(true)
                .queue(role -> topics.put(topicName, new Topic(topicName, role)));
    }

    /**
     * Deletes the Topic role from this server.
     * @param topic The Topic to remove
     */
    public void deleteTopic(Topic topic) {
        if (topic != null) {
            topic.getRole().delete().queue();
            topics.remove(topic.getName());
        }
    }

    /**
     * Deletes the Topic role from this server.
     * @param topicName The name of the Topic to remove
     */
    public void deleteTopic(String topicName) {
        deleteTopic(topics.get(topicName));
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
        return topics.values().toArray(new Topic[0]);
    }

    /**
     * Gets the Topic with the specified name
     * @param topicName The name of the Topic to retrieve
     * @return The Topic object, or null if the Topic does not exist
     */
    public Optional<Topic> getTopic(String topicName) {
        return Optional.ofNullable(topics.get(topicName));
    }
}
