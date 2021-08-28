package best.ollie.walle.commands.permissions;

import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandGroup;
import best.ollie.walle.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

/**
 * Permission group for all permissions
 */
public class PermissionGroup extends CommandGroup {

	public PermissionGroup() {
		super("perms", "", "Shows the help menu for the permissions group", "perms");
	}

}
