package best.ollie.walle.commands;

import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
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
  public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
    boolean commandRun = false;
    if (args.length == 0) {
      sendHelpMessage(event, prefix, permissions);
      commandRun = true;
    } else {
      for (Command command : getCommands()) { ;
        if (command.getName().equals(args[0])) {
          if (!CommandHandler.getInstance().hasPerm(event.getMember(), command.getPermission(), permissions)) {
            CommandHandler.getInstance().sendNoPermissionMessage(event.getChannel(), prefix, getName() + " " + command.getName());
            return;
          }
          commandRun = true;
          command.run(event, Arrays.copyOfRange(args,1,args.length), prefix, permissions);
        }
      }
    }
    if (!commandRun) {
      CommandHandler.getInstance().sendInvalidCommandMessage(event.getChannel(), prefix, getName() + " " +args[0]);
    }
  }

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
  public void sendHelpMessage(GuildMessageReceivedEvent event, String prefix, List<String> permissions) {
    StringBuilder sb = new StringBuilder();
    if (Util.canSendMessage(event.getChannel())) {
      //Grab the list of commands they have access too
      for (Command command : commands) {
        if (CommandHandler.getInstance().hasPerm(event.getMember(), command.getPermission(), permissions)) {
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

}
