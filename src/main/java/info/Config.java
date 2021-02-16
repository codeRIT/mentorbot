package info;

import java.util.Set;

/**
 * Contains configuration information, such as role and category names.
 */
public class Config {
    /**
     * Members with these roles will be able to use all commands and access all
     * mentoring rooms.
     */
    public static Set<String> ADMIN_ROLES = Set.of(
        "Director",
        "MLH"
    );

    /**
     * The prefix character used for bot commands.
     */
    public static String COMMAND_PREFIX = "$";
}
