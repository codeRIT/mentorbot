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

    /**
     * Map from room names to Room objects
     */
    private final HashMap<String, Room> rooms = new HashMap<>();

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
     * @param mentee The mentee for this room
     * @return The new Room
     */
    public Room createRoom(Member mentee) {
        // get the lowest unused number
        int number = 1;
        for (int i = 1; i <= rooms.size() + 1; i++) {
            if (rooms.get(Room.makeName(name, i)) == null) {
                number = i;
                break;
            }
        }

        Room room = new Room(this, number, mentee);
        rooms.put(Room.makeName(name, number), room);
        return room;
    }

    /**
     * Delete a Room
     * @param room The Room to delete
     */
    public void deleteRoom(Room room) {
        room.delete();
        rooms.remove(room.getName());
    }

    /**
     * Geets the Room with the specified name
     * @param roomName The name of the Room to retrieve
     * @return The Room object, or null if the Room does not exist
     */
    public Optional<Room> getRoom(String roomName) {
        return rooms.values().stream()
            .filter(r -> r.getName().toLowerCase().equals(roomName.toLowerCase()))
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
