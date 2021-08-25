package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.exceptions.ResultNotFoundException;
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

/**
 * Class to handle whenever a command is run and how we handle it
 */
public class CommandHandler implements EventListener {

    /**
     * Store the one instance of this class
     */
    private static CommandHandler instance;

    /**
     * Store the list of general commands
     */
    private final List<Command> commands = new ArrayList<>();

    /**
     * Store the list of command groups
     */
    private final List<CommandGroup> groups = new ArrayList<>();

    /**
     * Update the instance on creation
     */
    public CommandHandler() {
        instance = this;
    }

    /**
     * @return A list of general commands
     */
    public List<Command> getCommands() { return commands; }

    /**
     * @return A list of command groups
     */
    public List<CommandGroup> getGroups() { return groups; }

    /**
     * Register a command to be executed
     * @param c the command
     */
    public void registerCommand(Command c) {
        commands.add(c);
    }

    /**
     * Register a new command group
     * @param group the group to register
     */
    public void registerGroup(CommandGroup group) { groups.add(group); }

    /**
     * @return The instance of the the command handler
     */
    public static CommandHandler getInstance() { return instance; }

    /**
     * Check if a user has permission in a guild to run
     * @param user The user
     * @param guild The guild
     * @param permission The permission
     * @return true if can be run
     */
    public boolean hasPerm(Member user, Guild guild, String permission) {
        //Grab a list of roles the user has and add the everyone role as that is shared with everyone
        List<Role> roles = new ArrayList<>(user.getRoles());
        Role everyone = guild.getPublicRole();
        roles.add(everyone);
        List<String> permissions = new ArrayList<>();
        //Get all the permissions for each rolke
        for (Role role : roles) {
            try {
                permissions.addAll(Bot.driver.getPerms(role.getId()));
            } catch (ResultNotFoundException e) {
                Bot.logger.error("Failed permission check. Look into this");
            }
        }
        //Return true if they have the permission or user is an owner
        if (permissions.contains(permission) || user.isOwner()) {
            return true;
        } else return permissions.contains("*");
    }

    /**
     * Handle when a message is sent
     * @param event The event that happened
     */
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        //If a message is sent
        if (event instanceof GuildMessageReceivedEvent) {
            //Grab the event
            GuildMessageReceivedEvent eventNew = (GuildMessageReceivedEvent) event;
            //If we can't send a message in the channel exit.
            if (!Util.canSendMessage(eventNew.getChannel())) { return; }
            Guild guild = eventNew.getGuild();
            String message = eventNew.getMessage().getContentStripped();
            String prefix;
            try {
                prefix = Bot.driver.getPrefix(guild.getId());
            } catch (ResultNotFoundException e) {
                Bot.logger.error("Failed prefix grab for guild: " + guild.getId());
                return;
            }
            //If our message wasn't a command exit
            if (!message.startsWith(prefix)) {
                return;
            }
            //Cut the message down to the right size
            message = message.substring(prefix.length());

            //If the message is a command group
            for (CommandGroup commandGroup : groups) {
                //If it matches a command group
                if (message.split(" ")[0].equals(commandGroup.getName())) {
                    //If they don't have permission, exit
                    if (!hasPerm(eventNew.getMember(), eventNew.getGuild(), commandGroup.getPermission())) {
                        eventNew.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().setDescription("You don't have permission for this command").build()).queue();
                        return;
                    }
                    //Otherwise remove the command to grab the list of arguments
                    message = message.substring(commandGroup.getName().length()).trim();
                    String[] args = message.split(" ");
                    //Run the command group with the arguments
                    commandGroup.run(eventNew, args);
                    //Create a string of all arguments
                    StringBuilder armsg = new StringBuilder();
                    for (String s : args) {
                        armsg.append(s);
                    }
                    Bot.logger.info("Command '" + commandGroup.getName() + "' executed by " + eventNew.getAuthor().getName() + " With args: " + armsg);
                    return;
                }
            }

            //If the message was a general command, repeat the same process above.
            for (Command command : commands) {
                if (message.split(" ")[0].equals(command.getName())) {
                    if (!hasPerm(eventNew.getMember(), eventNew.getGuild(), command.getPermission())) {
                        eventNew.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().setDescription("You don't have permission for this command").build()).queue();
                        return;
                    }
                    message = message.substring(command.getName().length()).trim();
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
            //If command was not found, tell them the help message
            eventNew.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().appendDescription("**Invalid command!** " + prefix + "help for help.").build());
        }
    }
}
