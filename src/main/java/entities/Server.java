package entities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that provides Topic CRUD methods for a Guild.
 */
public class Server {
    private final Guild guild;
    private final ArrayList<Topic> topics = new ArrayList<>();

    public Server(Guild guild) {
        this.guild = guild;
    }

    @Override
    public int hashCode() {
        return guild.getName().hashCode();
    }

    /**
     * Constructs a Server object from a Guild's role list.
     * @param guild The guild to build from
     * @return A Server with all topics added
     */
    public static Server load(Guild guild) {
        Server server = new Server(guild);
        List<Role> roles = guild.getRoles();
        for (Role role : roles) {
            String name = role.getName();
            if (name.startsWith("Topic | ")) {
                server.topics.add(new Topic(name.substring(8)));
            }
        }
        return server;
    }

    /**
     * Creates a new Topic role in this server.
     * @param topic The Topic to add
     */
    public void createTopic(Topic topic) {
        guild.createRole()
                .setName("Topic | " + topic.getName())
                .setMentionable(true)
                .complete();
        topics.add(topic);
    }

    /**
     * Deletes the Topic role from this server.
     * @param topic The Topic to remove
     */
    public void deleteTopic(Topic topic) {
        guild.getRolesByName("Topic | " + topic.getName(), true)
                .forEach((role -> role.delete().complete()));
        topics.remove(topic);
    }

    /**
     * Gets all Topics from this Server
     * @return An array of Topics
     */
    public Topic[] getTopics() {
        return (Topic[]) topics.toArray();
    }
}
