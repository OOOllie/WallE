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
	public void run(GuildMessageReceivedEvent event, String[] args) {
    if (args.length == 0) {
    	sendHelpMessage(event);
		} else {
    	for (Command command : getCommands()) { ;
    		if (command.getName().equals(args[0])) {
    			if (!CommandHandler.getInstance().hasPerm(event.getMember(), event.getGuild(), command.getPermission())) {
						event.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().setDescription("You don't have permission for this command").build()).queue();
						return;
					}
    			command.run(event, Arrays.copyOfRange(args,1,args.length));
				}
			}
		}
	}
}
