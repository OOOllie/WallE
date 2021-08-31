package best.ollie.walle.commands.setup;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * The command for changing the prefix of the bot
 */
public class PrefixCommand extends Command {

	/**
	 * Command to change the prefix of the bot
	 */
	public PrefixCommand() {
		super("prefix", "(prefix)", "Change the prefix of the bot", "setup.prefix");
	}

	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		if (args.length != 1) {
			CommandHandler.getInstance().sendCommandUsageMessage(this, event.getChannel(), prefix);
			return;
		}

		if (args[0].length() > 3) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("prefix-too-long").replaceAll("\\{prefix}", prefix)
				.replaceAll("\\{newPrefix}", args[0])
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		Bot.getDriver().updatePrefix(event.getGuild().getId(), args[0]);
		CommandHandler.getInstance().sendMessage(Bot.getProperty("prefix-updated").replaceAll("\\{prefix}", prefix)
			.replaceAll("\\{newPrefix}", args[0])
			, Bot.getProperty("successColour"), event.getChannel());

	}
}
