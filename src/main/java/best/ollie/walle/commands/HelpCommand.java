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
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix) {
		//Store the list command groups as separate lists
		HashMap<String, List<Command>> categories = CommandHandler.getInstance().getCommandCategories(event.getMember());

		//Build the message
		EmbedBuilder eo = Util.getDefEmbedWithFooter();

		//For every category, create a new section inside the embved
		for (Map.Entry<String, List<Command>> key : categories.entrySet()) {
			StringBuilder sb = new StringBuilder();
			for (Command command : key.getValue()) {
				String name = command.getName();
				//Added so prefix shows before if not general command
				if (!key.getKey().equals("General") && !command.getName().equalsIgnoreCase(key.getKey())) name = key.getKey().toLowerCase() + " " + name;
				sb.append("**").append(prefix).append(name).append(" ").append(command.getArguments()).append("**  - ").append(command.getDescription()).append("\n");
			}
			eo.addField(key.getKey() + " Commands", sb.toString(),false);
		}
		//Set the title
		eo.setTitle("Help Menu");
		//Send the message
		event.getChannel().sendMessageEmbeds(eo.build()).queue();
	}


}

