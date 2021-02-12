package entities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.LinkedList;

/**
 * A topic for a server. Internally contains a queue of Members.
 */
public class Topic {
    public static final String PREFIX = "Topic | ";

    private final String name;
    private final Role role;
    private final LinkedList<QueueMember> queue = new LinkedList<>();

    /**
     * Constructs a new Topic object. This does not automatically create
     * the topic on the Discord server.
     * @param name The name of the topic.
     * @param role The Role that represents this in the guild
     */
    public Topic(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    /**
     * Add a Member to the back of the queue.
     * @param member The Member to add
     */
    public void addToQueue(QueueMember member) {
        queue.add(member);
    }

    /**
     * Remove a Member from their position in the queue.
     * @param member The Member to remove
     */
    public void removeFromQueue(Member member) {
        int index = queue.indexOf(new QueueMember(member));
        if (index != -1) {
            queue.remove(index);
        }
    }

    /**
     * Remove a QueueMember from their position in the queue.
     * @param member The QueueMember to remove
     */
    public void removeFromQueue(QueueMember member) {
        queue.remove(member);
    }

    /**
     * Check if a Member is inside the queue.
     * @param member The Member to check
     * @return True if the member is in the queue, false otherwise
     */
    public boolean isInQueue(Member member) {
        return queue.contains(new QueueMember(member));
    }

    /**
     * Returns an array of all QueueMembers in the queue.
     * @return The QueueMembers in this queue
     */
    public QueueMember[] getMembersInQueue() {
        return queue.toArray(new QueueMember[0]);
    }

    /**
     * Remove and return the next QueueMember in the queue.
     * @return The QueueMember at the front of the queue
     */
    public QueueMember getNextFromQueue() {
        return queue.remove();
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

    /**
     * Get the name of the role representing this Topic.
     * @return This Topic's role's name
     */
    public String getRoleName() {
        return PREFIX + name;
    }
}
