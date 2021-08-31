package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class MusicVolumeCommand extends Command {

	public MusicVolumeCommand() {
		super("volume", "(volume)", "Change the volume of the bot between 1 and 100", "music.volume");
	}

	/**
	 * @param event the event
	 * @param args the command arguments
	 * @param prefix the prefix of the bot
	 * @param permissions the permissions the command was run with
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {

		//Check we have an argument
		if (args.length != 1) {
			CommandHandler.getInstance().sendCommandUsageMessage(this, event.getChannel(), prefix);
			return;
		}

		//Check we can run the command
		if (!Bot.getManager().handleChecks(event.getMember(), event.getChannel(), prefix)) return;

		//Check the volume is actually a number
		AudioManager manager = event.getGuild().getAudioManager();
		int volume;
		try {
			volume = Integer.parseInt(args[0]);
		} catch (NumberFormatException exception) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("incorrect-volume")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{channel}", manager.getConnectedChannel().getName())
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		//Check the volume is between 1 and 100
		if (volume < 1 || volume > 100) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("incorrect-volume")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{channel}", manager.getConnectedChannel().getName())
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		//Change the colume
		Bot.getManager().getGuildAudioPlayer(event.getGuild()).scheduler.setVolume(volume);
		CommandHandler.getInstance().sendMessage(Bot.getProperty("volume-set")
				.replaceAll("\\{prefix}", prefix).replaceAll("\\{volume}", String.valueOf(volume))
			, Bot.getProperty("successColour"), event.getChannel());

	}
}
