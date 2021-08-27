package best.ollie.walle.commands.permissions;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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
			event.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter("0xFF0000").appendDescription(Bot.getProperty("invalid-role"))
				.build()).queue();
			return;
		}

		if (!CommandHandler.getInstance().getAllPermissions().contains(args[1])) {
			event.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter("0xFF0000").appendDescription(Bot.getProperty("invalid-permission"))
				.build()).queue();
			return;
		}

		Bot.driver.addPerm(role.getId(), args[1]);
		event.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter(Bot.getProperty("successColour")).appendDescription("Added permission").build()).queue();

	}
}
