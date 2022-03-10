package Harsho.Hype.Bot.Commands.LogCommands;

import Harsho.Hype.Bot.MySQL.GetData;
import Harsho.Hype.Bot.MySQL.UpdateData;
import Harsho.Hype.Bot.Storage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class SetLogCommand extends ListenerAdapter {
    private static String prefix;

    public static String help() {
        return prefix + "setlog {log channel} - sets log channel.";
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        String[] message = event.getMessage().getContentRaw().split("\\s+");
        long guildID = event.getGuild().getIdLong();
        prefix = GetData.getPrefix(guildID);
        Storage.PREFIXES.put(guildID, prefix);
        Collection<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.MANAGE_SERVER);
        if (message[0].equalsIgnoreCase(prefix + "setlog")) {
            Member author = event.getMember();
            assert author != null;
            if (!author.hasPermission(permissions)) {
                event.getChannel().sendMessage("You do not have the required permission to set the log channel")
                        .queue();
                return;
            }
            if (message.length < 2) {
                event.getChannel().sendMessage("Mention the channel").queue();
                return;
            }
            TextChannel logChannel = event.getMessage().getMentionedChannels().get(0);
            try {
                UpdateData.updateLog(guildID, logChannel.getIdLong());
                event.getChannel().sendMessage("Log channel has been set to " + logChannel.getAsMention()).queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}