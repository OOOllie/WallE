package best.ollie.walle.commands.permissions;

import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandGroup;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Permission group for all permissions
 */
public class PermissionGroup extends CommandGroup {

	public PermissionGroup() {
		super("perms", "", "Shows the help menu for the permissions group", "perms");
	}

	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix) {
		boolean commandRun = false;
    if (args.length == 0) {
    	sendHelpMessage(event, prefix);
		} else {
    	for (Command command : getCommands()) { ;
    		if (command.getName().equals(args[0])) {
    			if (!CommandHandler.getInstance().hasPerm(event.getMember(), command.getPermission())) {
						CommandHandler.getInstance().sendNoPermissionMessage(event.getChannel());
						return;
					}
    			command.run(event, Arrays.copyOfRange(args,1,args.length), prefix);
    			commandRun = true;
				}
			}
		}
    if (!commandRun) {
    	CommandHandler.getInstance().sendInvalidCommandMessage(event.getChannel());
		}
	}
}
