package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to add songs to the queue
 */
public class MusicAddCommand extends Command {

	/**
	 * Instantiate the command
	 */
	public MusicAddCommand() {
		super("add", "(search|link)", "Add the song to the queue", "music.add");
	}

	/**
	 * @param event the event
	 * @param args the command arguments
	 * @param prefix the prefix of the bot
	 * @param permissions the permissions the command was run with
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		Bot.getManager().runCommand(args, event, this, prefix, false);
	}

}
