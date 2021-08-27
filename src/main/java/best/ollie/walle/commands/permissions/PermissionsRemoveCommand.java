package best.ollie.walle.commands.permissions;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to remove permission from a role
 */
public class PermissionsRemoveCommand extends Command {

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
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix) {
		if (args.length != 2) {
			CommandHandler.getInstance().sendCommandUsageMessage(this, event.getChannel(), prefix);
			return;
		}

		Role role = Util.convertStringToRole(args[0], event.getGuild());
		if (role == null) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("invalid-role"), Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		if (!CommandHandler.getInstance().getAllPermissions().contains(args[1])) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("invalid-permission"), Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		List<String> permissions;
		try {
			permissions = Bot.driver.getPerms(role.getId());
		} catch (ResultNotFoundException exception) {
			Bot.logger.error("Failed to get permissions for valid role: " + role);
			return;
		}

		if (!permissions.contains(args[1])) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("permission-not-already"), Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		Bot.driver.removePerm(role.getId(), args[1]);
		CommandHandler.getInstance().sendMessage(Bot.getProperty("added-permission"), Bot.getProperty("successColour"), event.getChannel());

	}
}
