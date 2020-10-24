package listeners;

import entities.Server;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MainEventListener extends ListenerAdapter {
    /**
     * A function that handles a single command.
     */
    private interface CommandHandler {
        /**
         * Handle a single command.
         * @param member The member that called the command
         * @param channel The channel that the command was called in
         * @param server The Server that the command was called in
         * @param args Extra command arguments, if any
         */
        void handle(@NotNull Member member, TextChannel channel, Server server, String[] args);
    }

    private final HashMap<String, Server> servers = new HashMap<>();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Logged in");
        event.getJDA().getPresence().setPresence(Activity.playing("$help"), false);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] tokens = event.getMessage().getContentDisplay().split(" ");
        if (!tokens[0].startsWith("$")) return;

        String guildID = event.getGuild().getId();
        Server server = servers.computeIfAbsent(guildID, k -> Server.load(event.getGuild()));

        // pick the correct method to call
        CommandHandler commandHandler;
        switch (tokens[0].substring(1)) {
            case "help" -> commandHandler = this::help;
            default -> commandHandler = this::unknownCommand;
        }
        commandHandler.handle(
                Objects.requireNonNull(event.getMember()),
                event.getChannel(),
                server,
                Arrays.copyOfRange(tokens, 1, tokens.length));
    }

    private void help(Member member, TextChannel channel, Server server, String[] args) {
        channel.sendMessage(member.getAsMention() + " No commands implemented yet!").complete();
    }

    private void unknownCommand(Member member, TextChannel channel, Server server, String[] args) {
        channel.sendMessage(member.getAsMention() + " Command does not exist! Try $help for a list of valid commands.").complete();
    }
}
