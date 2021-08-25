package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandGroup extends Command {

  private final List<Command> commands = new ArrayList<>();

  public CommandGroup(String name, String arguments, String description, String permission) {
    super(name, arguments, description, permission);
  }

  public abstract void run(GuildMessageReceivedEvent event, String[] args);

  public void registerCommand(Command command) { commands.add(command); }

  public List<Command> getCommands() { return commands; }

  public void sendHelpMessage(GuildMessageReceivedEvent event) {
    StringBuilder sb = new StringBuilder();
    String prefix = Bot.driver.getPrefix(event.getGuild().getId());
    for (Command command : commands) {
      if (CommandHandler.getInstance().hasPerm(event.getMember(), event.getGuild(), command.getPermission())) {
        String commandString = "**" + prefix + getName() + " " + command.getName() + " " + command.getArguments() + "**  - " + command.getDescription() + "\n";
        sb.append(commandString);
      }
    }
    String name = getName();
    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    EmbedBuilder eo = Util.getDefEmbedWithFooter();
    eo.addField(name + " Menu",sb.toString(),false);
    if (Util.canSendMessage(event.getChannel())) {
      event.getChannel().sendMessageEmbeds(eo.build()).queue();
    }
  }

}
