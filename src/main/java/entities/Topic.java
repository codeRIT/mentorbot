package entities;

import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;

/**
 * A topic for a server. Internally contains a queue of Members.
 */
public class Topic {
    private final String name;
    private final ArrayList<Member> queue = new ArrayList<>();

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
     * Remove and return the next Member in the queue.
     * @return The Member at the front of the queue
     */
    public Member getNextFromQueue() {
        return queue.remove(0);
    }

    /**
     * Get the name of this Topic.
     * @return This Topic's name
     */
    public String getName() {
        return name;
    }
}
