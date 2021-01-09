package entities;

import net.dv8tion.jda.api.entities.Member;

import java.util.LinkedList;

/**
 * A topic for a server. Internally contains a queue of Members.
 */
public class Topic {
    public static final String PREFIX = "Topic | ";

    private final String name;
    private final LinkedList<Member> queue = new LinkedList<>();

    /**
     * Constructs a new Topic object. This does not automatically create
     * the topic on the Discord server.
     * @param name The name of the topic.
     */
    public Topic(String name) {
        this.name = name;
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
     * Remove and return the next Member in the queue.
     * @return The Member at the front of the queue
     */
    public Member getNextFromQueue() {
        return queue.remove();
    }

    /**
     * Get the name of this Topic.
     * @return This Topic's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the name of the role representing this Topic.
     * @return This Topic's role's name
     */
    public String getRoleName() {
        return PREFIX + name;
    }
}
