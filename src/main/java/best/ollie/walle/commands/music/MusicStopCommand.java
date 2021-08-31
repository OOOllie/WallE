package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to stop everything and clear the queue
 */
public class MusicStopCommand extends Command {

	/**
	 * Initiate the command
	 */
	public MusicStopCommand() {
		super("stop", "", "Stop all music activities", "music.stop");
	}

	/**
	 * @param event the event
	 * @param args the command arguments
	 * @param prefix the prefix of the bot
	 * @param permissions the permissions the command was run with
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		if (!Bot.getManager().handleChecks(event.getMember(), event.getChannel(), prefix)) return;

		Bot.getManager().getGuildAudioPlayer(event.getGuild()).scheduler.stop();
		CommandHandler.getInstance().sendMessage(Bot.getProperty("music-cleared")
				.replaceAll("\\{prefix}", prefix)
			, Bot.getProperty("successColour"), event.getChannel());
	}
}
