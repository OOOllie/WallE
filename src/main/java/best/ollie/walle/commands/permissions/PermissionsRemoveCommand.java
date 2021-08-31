package best.ollie.walle.commands.permissions;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command to remove permission from a role
 */
public class PermissionsRemoveCommand extends Command {

	/**
	 * Store the logger object for the class
	 */
	private final Logger logger = LogManager.getLogger(PermissionsRemoveCommand.class);

	/**
	 * Initiate the command
	 */
	public PermissionsRemoveCommand() {
		super("remove", "(role | permission)","Remove a permission to a specific role", "perms.remove");
	}

	/**
	 * The method body for the command
	 * @param event The event containing important data
	 * @param args The arguments ran with the command
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		if (args.length != 2) {
			CommandHandler.getInstance().sendCommandUsageMessage(this, event.getChannel(), prefix);
			return;
		}

		Role role = Util.convertStringToRole(args[0], event.getGuild());
		if (role == null) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("invalid-role")
				.replaceAll("\\{prefix}", prefix).replaceAll("\\{role}", args[0])
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		if (!Bot.allPerms.contains(args[1])) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("invalid-permission")
				.replaceAll("\\{prefix}", prefix).replaceAll("\\{permission}", args[1])
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		List<String> rolePerms;
		try {
			rolePerms = Bot.getDriver().getPerms(role.getId());
		} catch (ResultNotFoundException exception) {
			logger.error("Failed to get permissions for valid role: " + role);
			return;
		}

		if (!rolePerms.contains(args[1])) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("permission-not-added")
				.replaceAll("\\{prefix}", prefix).replaceAll("\\{permission}", args[1])
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		Bot.getDriver().removePerm(role.getId(), args[1]);
		CommandHandler.getInstance().sendMessage(Bot.getProperty("removed-permission")
			.replaceAll("\\{prefix}", prefix).replaceAll("\\{permission}", args[1]).replaceAll("\\{role}", role.getAsMention())
			, Bot.getProperty("successColour"), event.getChannel());

	}
}
