package entities;

import net.dv8tion.jda.api.entities.Member;

/**
 * Describes a member of a topic queue.
 */
public class QueueMember {
    private final Member member;
    private final String message;

    /**
     * Constructs a new QueueMember object.
     * @param member The member that this object represents
     * @param message The member's queue message
     */
    public QueueMember(Member member, String message) {
        this.member = member;
        this.message = message;
    }

    /**
     * Constructs a new QueueMember object without a message. Useful for
     * checking Member equality.
     * @param member The member that this object represents
     */
    public QueueMember(Member member) {
        this(member, null);
    }

    /**
     * Gets the member represented by this object.
     * @return This object's member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Gets this member's message.
     * @return This member's message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Member) {
            return member.equals(obj);
        } else if (obj instanceof QueueMember) {
            return member.equals(((QueueMember)obj).member);
        } else {
            return super.equals(obj);
        }
    }
}
