package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic command group to split lists of commands
 */
public abstract class CommandGroup extends Command {

  /**
   * Store the list of commands the group has
   */
  private final List<Command> commands = new ArrayList<>();

  /**
   * @param name Name of command group
   * @param arguments The different types of arguments/commands it takes
   * @param description The description of the group
   * @param permission The permission required to run it
   */
  public CommandGroup(String name, String arguments, String description, String permission) {
    super(name, arguments, description, permission);
  }

  /**
   * Method to implement for the each command group
   * @param event The event that was run
   * @param args The arguments it was ran with
   */
  public abstract void run(GuildMessageReceivedEvent event, String[] args);

  /**
   * Add a new command to a group
   * @param command the command
   */
  public void registerCommand(Command command) { commands.add(command); }

  /**
   * @return List of commands
   */
  public List<Command> getCommands() { return commands; }

  /**
   * Create a help message for that group
   * @param event The event to retain information about
   */
  public void sendHelpMessage(GuildMessageReceivedEvent event) {
    StringBuilder sb = new StringBuilder();
    Guild guild = event.getGuild();
    String prefix;
    try {
      prefix = Bot.driver.getPrefix(guild.getId());
    } catch (ResultNotFoundException e) {
      Bot.logger.error("Failed to fetch prefix for: " + guild.getId());
      e.printStackTrace();
      return;
    }
    //Grab the list of commands they have access too
    for (Command command : commands) {
      if (CommandHandler.getInstance().hasPerm(event.getMember(), guild, command.getPermission())) {
        String commandString = "**" + prefix + getName() + " " + command.getName() + " " + command.getArguments() + "**  - " + command.getDescription() + "\n";
        sb.append(commandString);
      }
    }
    //Create a message
    String name = getName();
    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    EmbedBuilder eo = Util.getDefEmbedWithFooter();
    eo.addField(name + " Menu",sb.toString(),false);
    event.getChannel().sendMessageEmbeds(eo.build()).queue();
  }

}
