package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements EventListener {

    private static CommandHandler instance;

    private final List<Command> commands = new ArrayList<>();

    private final List<CommandGroup> groups = new ArrayList<>();

    public CommandHandler() {
        instance = this;
    }

    public List<Command> getCommands() { return commands; }

    public List<CommandGroup> getGroups() { return groups; }

    public void registerCommand(Command c) {
        commands.add(c);
    }

    public void registerGroup(CommandGroup group) { groups.add(group); }

    public static CommandHandler getInstance() { return instance; }

    public boolean hasPerm(Member user, Guild guild, String permission) {
        List<Role> roles = new ArrayList<>(user.getRoles());
        Role everyone = guild.getPublicRole();
        roles.add(everyone);
        List<String> permissions = new ArrayList<>();
        for (Role role : roles) {
            permissions.addAll(Bot.driver.getPerms(role.getId()));
        }
        if (permissions.contains(permission) || user.isOwner()) {
            return true;
        } else return permissions.contains("*");
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent eventNew = (GuildMessageReceivedEvent) event;
            if (!Util.canSendMessage(eventNew.getChannel())) { return; }
            Guild guild = eventNew.getGuild();
            String message = eventNew.getMessage().getContentStripped();
            String prefix = Bot.driver.getPrefix(guild.getId());
            if (!message.startsWith(prefix)) {
                return;
            }
            message = message.substring(prefix.length());

            for (CommandGroup commandGroup : groups) {
                System.out.println("Test");
                if (message.split(" ")[0].equals(commandGroup.getName())) {
                    if (!hasPerm(eventNew.getMember(), eventNew.getGuild(), commandGroup.getPermission())) {
                        eventNew.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().setDescription("You don't have permission for this command").build()).queue();
                        return;
                    }
                    message = message.substring(commandGroup.getName().length());
                    String[] args = message.split(" ");
                    commandGroup.run(eventNew, args);
                    StringBuilder armsg = new StringBuilder();
                    for (String s : args) {
                        armsg.append(s);
                    }
                    Bot.logger.info("Command '" + commandGroup.getName() + "' executed by " + eventNew.getAuthor().getName() + " With args: " + armsg);
                    return;
                }
            }

            for (Command command : commands) {
                if (message.split(" ")[0].equals(command.getName())) {
                    if (!hasPerm(eventNew.getMember(), eventNew.getGuild(), command.getPermission())) {
                        eventNew.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().setDescription("You don't have permission for this command").build()).queue();
                        return;
                    }
                    message = message.substring(command.getName().length());
                    String[] args = message.split(" ");
                    command.run(eventNew, args);
                    StringBuilder armsg = new StringBuilder();
                    for (String s : args) {
                        armsg.append(s);
                    }
                    Bot.logger.info("Command '" + command.getName() + "' executed by " + eventNew.getAuthor().getName() + " With args: " + armsg);
                    return;
                }
            }
            eventNew.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().appendDescription("**Invalid command!** " + Bot.driver.getPrefix(guild.getId()) + "help for help.").build());
        }
    }
}
