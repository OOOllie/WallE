package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to skip songs
 */
public class MusicSkipCommand extends Command {

	/**
	 * Initiate the command
	 */
	public MusicSkipCommand() {
		super("skip", "", "Skip the current song", "music.skip");
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

		GuildMusicManager musicManager = Bot.getManager().getGuildAudioPlayer(event.getGuild());
		if (musicManager.scheduler.getCurrentTrack() == null) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("no-track-playing").replaceAll("\\{prefix}",prefix),
				Bot.getProperty("errorColour"), event.getChannel());
			return;
		}
		CommandHandler.getInstance().sendMessage(Bot.getProperty("music-skip")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{song}", musicManager.scheduler.getCurrentTrack().getInfo().title)
				, Bot.getProperty("successColour"), event.getChannel());
		Bot.getManager().skipTrack(event.getGuild());
	}
}
