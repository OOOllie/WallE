package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to pause the music playing
 */
public class MusicPauseCommand extends Command {

	/**
	 * Initiate the command
	 */
	public MusicPauseCommand() {
		super("pause", "", "Pause or play the current music", "music.pause");
	}

	/**
	 * @param event the event
	 * @param args the command arguments
	 * @param prefix the prefix of the bot
	 * @param permissions the permissions the command was run with
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		//If we can run the command
		if (!Bot.getManager().handleChecks(event.getMember(), event.getChannel(), prefix)) return;

		//If its paused, resume it, otherwise pause it
		GuildMusicManager musicManager = Bot.getManager().getGuildAudioPlayer(event.getGuild());
		if (musicManager.scheduler.isPaused()) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("music-resumed")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{song}", musicManager.scheduler.getCurrentTrack().getInfo().title)
				, Bot.getProperty("successColour"), event.getChannel());
		} else {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("music-paused")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{song}", musicManager.scheduler.getCurrentTrack().getInfo().title)
				, Bot.getProperty("successColour"), event.getChannel());
		}
		musicManager.scheduler.pause();
	}
}
