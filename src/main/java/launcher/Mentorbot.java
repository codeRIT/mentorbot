package launcher;

import listeners.MainEventListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;

import javax.security.auth.login.LoginException;

public class Mentorbot {
    public static void main(String[] args) {
        try {
            JDABuilder.createDefault(System.getenv("MENTORBOT_TOKEN"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new MainEventListener())
                .build();
        } catch (LoginException ex) {
            ex.printStackTrace();
        }
    }
}
