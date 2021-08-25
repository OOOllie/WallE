package best.ollie.walle.commands.permissions;

import best.ollie.walle.commands.CommandGroup;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Permission group for all permissions
 */
public class PermissionGroup extends CommandGroup {

	public PermissionGroup() {
		super("perms", "", "Shows the help menu for the permissions group", "perms");
	}

	@Override
	public void run(GuildMessageReceivedEvent event, String[] args) {
    if (args.length == 1) {
    	sendHelpMessage(event);
		}
	}
}
