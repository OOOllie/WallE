package best.ollie.walle.commands.permissions;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandGroup;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command to list all permissions available
 */
public class PermissionsListCommand extends Command {

	public PermissionsListCommand() {
		super("list", "", "List all current permissions available.", "perms.list");
	}

	/**
	 * @param event the event of the command
	 * @param args the arguments of the command
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix) {
		//Store the list command groups as separate lists
		HashMap<String, List<Command>> categories = new HashMap<>();
		//Create a general category for commands without a group
		categories.put("General", new ArrayList<>(CommandHandler.getInstance().getCommands()));

		//For every command group command, add them as a list
		for (CommandGroup commandGroup : CommandHandler.getInstance().getGroups()) {
				List<Command> accessCommands = new ArrayList<>(commandGroup.getCommands());
				//Make sure default command is added
				accessCommands.add(commandGroup);
				categories.put(commandGroup.getName().substring(0,1).toUpperCase() + commandGroup.getName().substring(1).toLowerCase(), accessCommands);
		}

		//Build the message
		EmbedBuilder eo = Util.getDefEmbedWithFooter();

		//For every category, create a new section inside the embved
		for (Map.Entry<String, List<Command>> key : categories.entrySet()) {
			StringBuilder sb = new StringBuilder();
			for (Command command : key.getValue()) {
				String name = command.getName();
				//Added so prefix shows before if not general command
				if (!key.getKey().equals("General") && !command.getName().equalsIgnoreCase(key.getKey())) name = key.getKey().toLowerCase() + " " + name;
				sb.append("**").append(prefix).append(name).append(" ").append(command.getArguments()).append("**  - ").append(command.getPermission()).append("\n");
			}
			eo.addField(key.getKey() + " Permissions", sb.toString(),false);
		}
		//Set the title
		eo.setTitle("Permissions List");
		//Send the message
		event.getChannel().sendMessageEmbeds(eo.build()).queue();
	}
}
