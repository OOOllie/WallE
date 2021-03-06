package best.ollie.walle.commands.permissions;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandGroup;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command to list all permissions available
 */
public class PermissionsListCommand extends Command {

	/**
	 * Store the logger object
	 */
	private final Logger logger = LogManager.getLogger(PermissionsListCommand.class);

	public PermissionsListCommand() {
		super("list", "[roleId]", "List all current permissions available.", "perms.list");
	}

	/**
	 * @param event the event of the command
	 * @param args the arguments of the command
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		//Send a list of all permissions
		if (args.length == 0) {
			//Store the list command groups as separate lists
			HashMap<String, List<Command>> categories = new HashMap<>();
			//Create a general category for commands without a group
			categories.put("General", new ArrayList<>(CommandHandler.getInstance().getCommands()));

			//For every command group command, add them as a list
			for (CommandGroup commandGroup : CommandHandler.getInstance().getGroups()) {
				List<Command> accessCommands = new ArrayList<>(commandGroup.getCommands());
				//Make sure default command is added
				accessCommands.add(commandGroup);
				categories.put(commandGroup.getName().substring(0, 1).toUpperCase() + commandGroup.getName().substring(1).toLowerCase(), accessCommands);
			}

			//Build the message
			EmbedBuilder eo = Util.getDefEmbedWithFooter();

			//For every category, create a new section inside the embved
			for (Map.Entry<String, List<Command>> key : categories.entrySet()) {
				StringBuilder sb = new StringBuilder();
				for (Command command : key.getValue()) {
					sb.append("**").append(prefix).append(command.getName()).append(" ").append(command.getArguments()).append("**  - ")
						.append(command.getPermission()).append("\n");
				}
				eo.addField(key.getKey() + " Permissions", sb.toString(), false);
			}
			//Set the title
			eo.setTitle("Permissions List");
			//Send the message
			if (Util.canSendMessage(event.getChannel())) {
				event.getChannel().sendMessageEmbeds(eo.build()).queue();
			}
		} else if (args.length == 1) {
			Role role = Util.convertStringToRole(args[0], event.getGuild());
			if (role == null) {
				CommandHandler.getInstance().sendMessage(Bot.getProperty("invalid-role")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{role}", args[0])
					, Bot.getProperty("errorColour"), event.getChannel());
				return;
			}
			List<String> rolePerms;
			try {
				rolePerms = Bot.getDriver().getPerms(role.getId());
			} catch (ResultNotFoundException exception) {
				logger.error("Failed to get permissions for valid role: " + role);
				return;
			}
			if (Util.canSendMessage(event.getChannel())) {
				EmbedBuilder builder = Util.getDefEmbedWithFooter();
				builder.setTitle(role.getName() + " Permissions");
				StringBuilder stringBuilder = new StringBuilder();
				if (rolePerms.size() == 0) {
					stringBuilder.append("No permissions!");
				} else {
					for (String permission : rolePerms) {
						stringBuilder.append("- " + permission + " \n");
					}
				}
				builder.setDescription(stringBuilder.toString());
				event.getChannel().sendMessageEmbeds(builder.build()).queue();
			}
		} else {
			CommandHandler.getInstance().sendCommandUsageMessage(this, event.getChannel(), prefix);
		}

	}
}
