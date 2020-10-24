package launcher;

import listeners.MainEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;

import javax.security.auth.login.LoginException;

public class Lehrry {
    public static void main(String[] args) {
        try {
            JDA jda = JDABuilder.createDefault(System.getenv("LEHRRY_TOKEN"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new MainEventListener())
                    .build();
        } catch (LoginException ex) {
            ex.printStackTrace();
        }
    }
}
