package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List all currently available commands
 */
public class HelpCommand extends Command {
	public HelpCommand() {
		super("help", "", "Shows you the help menu", "help");
	}

	/**
	 * @param event The message event to gain access to the channel etc.
	 * @param args The arguments run with the command
	 */
	public void run(GuildMessageReceivedEvent event, String[] args) {
		//Store the list command groups as separate lists
		HashMap<String, List<Command>> categories = new HashMap<>();
		//Create a general category for commands without a group
		categories.put("General", new ArrayList<>());
		List<Command> commands = CommandHandler.getInstance().getCommands();
		List<CommandGroup> groups = CommandHandler.getInstance().getGroups();

		//For every command the user has permission for, add it to the general list
		for (Command command : commands) {
			if (CommandHandler.getInstance().hasPerm(event.getMember(), event.getGuild(),command.getPermission())) {
				categories.get("General").add(command);
			}
		}

		//For every command group command, add them as a list
		for (CommandGroup commandGroup : groups) {
			//If the user can access the command group
			if (CommandHandler.getInstance().hasPerm(event.getMember(), event.getGuild(),commandGroup.getPermission())) {
				List<Command> accessCommands = new ArrayList<>();
				//Get a list of all command group commands they can access within that group
				for (Command command : commandGroup.getCommands()) {
					if (CommandHandler.getInstance().hasPerm(event.getMember(), event.getGuild(), command.getPermission())) {
						accessCommands.add(command);
					}
				}
				//Make sure default command is added
				accessCommands.add(commandGroup);
				categories.put(commandGroup.getName().substring(0,1).toUpperCase() + commandGroup.getName().substring(1).toLowerCase(), accessCommands);
			}
		}

		//Grab the prefix
		String prefix;
		try {
			prefix = Bot.driver.getPrefix(event.getGuild().getId());
		} catch (ResultNotFoundException e) {
			Bot.logger.error("Failed to get prefix for help command, exiting execution!");
			return;
		}

		//Build the message
		EmbedBuilder eo = Util.getDefEmbedWithFooter();

		//For every category, create a new section inside the embved
		for (Map.Entry<String, List<Command>> key : categories.entrySet()) {
			StringBuilder sb = new StringBuilder();
			for (Command command : key.getValue()) {
				String name = command.getName();
				//Added so prefix shows before if not general command
				if (!name.equals("General") && !command.getName().equals(key.getKey())) name = key.getKey() + " " + name;
				sb.append("**").append(prefix).append(command.getName()).append(" ").append(command.getArguments()).append("**  - ").append(command.getDescription()).append("\n");
			}
			eo.addField(key.getKey() + " Commands", sb.toString(),false);
		}
		//Set the title
		eo.setTitle("Help Menu");
		//Send the message
		event.getChannel().sendMessageEmbeds(eo.build()).queue();
	}


}

