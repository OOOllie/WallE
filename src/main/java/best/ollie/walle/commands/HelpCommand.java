package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCommand extends Command {
	public HelpCommand() {
		super("help", "", "Shows you the help menu", "help");
	}

	public void run(GuildMessageReceivedEvent event, String[] args) {

		HashMap<String, List<Command>> categories = new HashMap<>();
		categories.put("General", new ArrayList<>());
		List<Command> commands = CommandHandler.getInstance().getCommands();
		List<CommandGroup> groups = CommandHandler.getInstance().getGroups();

		for (Command command : commands) {
			if (!CommandHandler.getInstance().hasPerm(event.getMember(), event.getGuild(),command.getPermission())) {
				continue;
			}
			categories.get("General").add(command);
		}

		for (CommandGroup commandGroup : groups) {
			categories.put(commandGroup.getName(), new ArrayList<>());
			categories.get(commandGroup.getName()).add(commandGroup);
			for (Command command : commandGroup.getCommands()) {
				categories.get(commandGroup.getName()).add(command);
			}
		}

		String prefix = Bot.driver.getPrefix(event.getGuild().getId());

		EmbedBuilder eo = Util.getDefEmbedWithFooter();

		for (Map.Entry<String, List<Command>> key : categories.entrySet()) {
			StringBuilder sb = new StringBuilder();
			for (Command command : key.getValue()) {
				sb.append("**").append(prefix).append(command.getName()).append(" ").append(command.getArguments()).append("**  - ").append(command.getDescription()).append("\n");
			}
			eo.addField(key.getKey() + " Commands", sb.toString(),false);
		}
		eo.setTitle("Help Menu");
		event.getChannel().sendMessageEmbeds(eo.build()).queue();
	}


}

