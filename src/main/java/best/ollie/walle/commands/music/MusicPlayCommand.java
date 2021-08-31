package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to play song immediately
 */
public class MusicPlayCommand extends Command {

	/**
	 * Initiate the command
	 */
	public MusicPlayCommand() {
		super("play", "(search|link)", "Play the song now before returning to queue", "music.play");
	}

	/**
	 * @param event the event
	 * @param args the command arguments
	 * @param prefix the prefix of the bot
	 * @param permissions the permissions the command was run with
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		Bot.getManager().runCommand(args, event, this, prefix, true);
	}

}