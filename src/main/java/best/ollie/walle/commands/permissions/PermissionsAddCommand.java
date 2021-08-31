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
 * Command to add permission to a role
 */
public class PermissionsAddCommand extends Command {

	/**
	 * Initiate the command
	 */
	public PermissionsAddCommand() {
		super("add", "(role | permission)","Add a permission to a specific role", "perms.add");
	}

	/**
	 * Store the logger object for the command
	 */
	private final Logger logger = LogManager.getLogger(PermissionsAddCommand.class);

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

		if (rolePerms.contains(args[1])) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("permission-added-already")
				.replaceAll("\\{prefix}", prefix).replaceAll("\\{permission}", args[1])
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		Bot.getDriver().addPerm(role.getId(), args[1]);
		CommandHandler.getInstance().sendMessage(Bot.getProperty("added-permission")
			.replaceAll("\\{prefix}", prefix).replaceAll("\\{permission}", args[1]).replaceAll("\\{role}",role.getAsMention())
			, Bot.getProperty("successColour"), event.getChannel());

	}
}
