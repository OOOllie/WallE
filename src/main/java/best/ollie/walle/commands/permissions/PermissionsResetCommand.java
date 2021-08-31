package best.ollie.walle.commands.permissions;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Command to reset permissions for a role
 */
public class PermissionsResetCommand extends Command {

	/**
	 * Initiate the command
	 */
	public PermissionsResetCommand() {
		super("reset", "(role)","Reset a permissions role", "perms.reset");
	}

	/**
	 * The method body for the command
	 * @param event The event containing important data
	 * @param args The arguments ran with the command
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		if (args.length != 1) {
			CommandHandler.getInstance().sendCommandUsageMessage(this, event.getChannel(), prefix);
			return;
		}

		Role role = Util.convertStringToRole(args[0], event.getGuild());
		if (role == null) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("invalid-role")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{role}", role.getAsMention())
				, Bot.getProperty("errorColour"), event.getChannel());
			return;
		}

		Bot.getDriver().removePermRole(role.getId());
		CommandHandler.getInstance().sendMessage(Bot.getProperty("reset-permission")
			.replaceAll("\\{prefix}", prefix).replaceAll("\\{role}", role.getAsMention())
			, Bot.getProperty("successColour"), event.getChannel());

	}
}
