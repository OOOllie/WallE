package best.ollie.walle.commands.setup;

import best.ollie.walle.commands.CommandGroup;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Group for setup commands for the bot
 */
public class SetupGroup extends CommandGroup {

	/**
	 * Initialise the setup group
	 */
	public SetupGroup() {
		super("setup", "", "The commands for the setup of the bot", "setup");
	}

}
