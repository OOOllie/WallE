package best.ollie.walle.commands.permissions;

import best.ollie.walle.commands.Command;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command to add permission to a role
 */
public class PermissionsAddCommand extends Command {

	/**
	 * Initiate the command
	 */
	public PermissionsAddCommand() {
		super("add", "(roleID | permission)","Add a permission to a specific role", "perms.add");
	}

	/**
	 * The method body for the command
	 * @param event The event containing important data
	 * @param args The arguments ran with the command
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args) {
		event.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().setDescription("This doesn't really do anything, just a quick test").build()).queue();
	}
}
