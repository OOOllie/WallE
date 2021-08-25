package best.ollie.walle.commands.permissions;

import best.ollie.walle.commands.CommandGroup;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Permission group for all permissions
 */
public class PermissionGroup extends CommandGroup {

	public PermissionGroup() {
		super("perms", "(add | remove | list | all)", "Handle the bot permissions for the server", "perms");
	}

	@Override
	public void run(GuildMessageReceivedEvent event, String[] args) {
    if (args.length == 0) {
    	sendHelpMessage(event);
		}
	}
}
