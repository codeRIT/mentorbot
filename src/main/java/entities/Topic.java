package entities;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

/**
 * A topic for a server. Internally contains a queue of Members.
 */
public class Topic {
    private final String name;
    private final Role role;
    private final Category category;
    private final LinkedList<Member> queue = new LinkedList<>();
    private final HashMap<Integer, Room> rooms = new HashMap<>();

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
     * Remove and return the next Member in the queue.
     * @return The new mentee
     */
    public Member getNextFromQueue() {
        return queue.remove();
    }

    /**
     * Create a new mentoring room for this topic.
     * @param mentor The mentor for this room
     * @param mentee The mentee for this room
     * @return The new Room
     */
    public Room createRoom(Member mentee) {
        // get the lowest unused number
        int number = 1;
        for (int i = 1; i <= rooms.size(); i++) {
            if (rooms.get((Integer)i) == null) {
                number = i;
                break;
            }
        }

        Room room = new Room(this, number, mentee);
        rooms.put(number, room);
        return room;
    }

    public void deleteRoom(Room room) {
        rooms.remove(room.getNumber());
    }

    public Optional<Room> getRoom(String name) {
        return rooms.values().stream()
            .filter(r -> r.getName().equals(name))
            .findFirst();
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

    public Category getCategory() {
        return category;
    }
}
