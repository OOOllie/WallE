package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to remove song from queue
 */
public class MusicRemoveCommand extends Command {

	/**
	 * Initiate the command
	 */
	public MusicRemoveCommand() {
		super("remove", "(queue-number)", "Remove a specific song from the queue", "music.remove");
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

		//Check we have a number to remove
		if (args.length != 1) {
			CommandHandler.getInstance().sendCommandUsageMessage(this, event.getChannel(), prefix);
			return;
		}

		//Check the queue isnt empty
		GuildMusicManager manager = Bot.getManager().getGuildAudioPlayer(event.getGuild());
		int size = manager.scheduler.getQueue().size();
		if (size == 0) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("queue-empty")
					.replaceAll("\\{prefix}", prefix)
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		//Check we were given a number
		int remove;
		try {
			remove = Integer.parseInt(args[0]);
		} catch (NumberFormatException exception) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("music-remove-not-number")
					.replaceAll("\\{prefix}", prefix)
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		//Check the number is in the queue
		if (remove < 1 || remove > size) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("music-remove-too-big")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{size}", String.valueOf(size))
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		//Remove the song
		CommandHandler.getInstance().sendMessage(Bot.getProperty("music-song-removed")
				.replaceAll("\\{prefix}", prefix).replaceAll("\\{song}", manager.scheduler.getQueue().get(remove - 1).getInfo().title)
			, Bot.getProperty("successColour"), event.getChannel());
		manager.scheduler.remove(remove - 1);

	}
}
