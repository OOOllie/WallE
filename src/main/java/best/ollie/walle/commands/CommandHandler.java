package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
     * Store the logger object for the class
     */
    private final Logger logger = LogManager.getLogger(CommandHandler.class);

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
     * @param member The user
     * @param permission The permission
     * @return true if can be run
     */
    public boolean hasPerm(Member member, String permission, List<String> permissions) {
        //Return true if they have the permission or user is an owner
        if (permissions.contains(permission) || member.isOwner()) {
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
            Guild guild = eventNew.getGuild();
            Member member = eventNew.getMember();
            String message = eventNew.getMessage().getContentRaw();
            String prefix;
            try {
                prefix = Bot.getDriver().getPrefix(guild.getId());
            } catch (ResultNotFoundException e) {
                logger.error("Failed prefix grab for guild: " + guild.getId());
                return;
            }
            //If our message wasn't a command exit
            if (!message.startsWith(prefix)) {
                return;
            }
            //Cut the message down to the right size
            message = message.substring(prefix.length());
            String command = message.split(" ")[0];
            //Get all the permissions straight away
            List<String> userPerms = getAllPerms(member);
            //If the message is a command group
            for (CommandGroup commandGroup : groups) {
                //If it matches a command group
                if (command.equals(commandGroup.getName())) {
                    //If they don't have permission, exit
                    if (!hasPerm(member, commandGroup.getPermission(), userPerms)) {
                        sendNoPermissionMessage(eventNew.getChannel(), prefix, commandGroup.getName());
                        return;
                    }
                    //Otherwise remove the command to grab the list of arguments
                    String[] args = getArguments(message, commandGroup.getName());
                    commandGroup.run(eventNew, args, prefix, userPerms);
                    logger.info("Command '" + commandGroup.getName() + "' executed by " + eventNew.getAuthor().getName() + " With args: " + getArgumentsLine(args));
                    return;
                }
            }

            //If the message was a general command, repeat the same process above.
            for (Command cmd : commands) {
                if (command.equals(cmd.getName())) {
                    if (!hasPerm(member, cmd.getPermission(), userPerms)) {
                        sendNoPermissionMessage(eventNew.getChannel(), prefix, cmd.getName());
                        return;
                    }
                    String[] args = getArguments(message, cmd.getName());
                    cmd.run(eventNew, args, prefix, userPerms);
                    logger.info("Command '" + cmd.getName() + "' executed by " + eventNew.getAuthor().getName() + " With args: " + getArgumentsLine(args));
                    return;
                }
            }
            //If command was not found, tell them the help message
            sendInvalidCommandMessage(eventNew.getChannel(), prefix, command);
        }
    }

    /**
     * Take a string of arguments and create an array of individual arguments
     * @param message the message
     * @param commandName the name of the command
     * @return the array of arguments
     */
    private String[] getArguments(String message, String commandName) {
        message = message.substring(commandName.length()).trim();
        return Arrays.stream(message.split(" ")).filter(arg -> arg.length() > 0).toArray(String[]::new);
    }

    /**
     * @param arguments the arguments array
     * @return the list of arguments as a single string
     */
    private String getArgumentsLine(String[] arguments) {
        StringBuilder armsg = new StringBuilder();
        for (String s : arguments) {
            armsg.append(s + " ");
        }
        return armsg.toString();
    }

    /**
     * @param member the member to see if they have permission
     * @return A list of commands in no group that a specific user has access to
     */
    public List<Command> getGeneralCommands(Member member, List<String> permissions) {
        logger.info("Getting all commands the user can access not in a group: " + member.getEffectiveName());
        List<Command> accessCommands = new ArrayList<>();
        for (Command command : commands) {
            if (permissions.contains(command.getPermission())) {
                accessCommands.add(command);
            }
        }
        return accessCommands;
    }

    /**
     * @param member the member to see if they have permission
     * @return A list of commands in the group that the member can access
     */
    public List<Command> getGroupCommands(Member member, CommandGroup group, List<String> permissions) {
        logger.info("Getting commands user: " + member.getEffectiveName() + " can access in: " + group.getName());
        List<Command> accessCommands = new ArrayList<>();
        //Get a list of all command group commands they can access within that group
        for (Command command : group.getCommands()) {
            if (permissions.contains(command.getPermission())) {
                accessCommands.add(command);
            }
        }
        return accessCommands;
    }

    /**
     * @param member The member to check for
     * @return The list of command categories that the member has access to
     */
    public HashMap<String,List<Command>> getCommandCategories(Member member) {
        logger.info("Getting command categories");
        HashMap<String, List<Command>> categories = new HashMap<>();
        //Create a general category for commands without a group
        List<String> permissions = getAllPerms(member);
        categories.put("General", getGeneralCommands(member, permissions));

        //For every command group command, add them as a list
        for (CommandGroup commandGroup : groups) {
            //If the user can access the command group
            if (permissions.contains(commandGroup.getPermission())) {
                List<Command> accessCommands = getGroupCommands(member, commandGroup, permissions);
                //Make sure default command is added
                accessCommands.add(0, commandGroup);
                categories.put(commandGroup.getName().substring(0,1).toUpperCase() + commandGroup.getName().substring(1).toLowerCase(), accessCommands);
            }
        }
        return categories;
    }

    /**
     * Send a normal message but with a colour
     * @param content The content
     * @param colour The colour
     * @param channel The channel
     */
    public void sendMessage(String content, String colour, TextChannel channel) {
        if (Util.canSendMessage(channel)) {
            channel.sendMessageEmbeds(Util.getDefEmbedWithFooter(colour).appendDescription(content).build()).queue();
        }
    }


    /**
     * Send a normal message
     * @param content The content
     * @param channel The channel
     */
    public void sendMessage(String content, TextChannel channel) {
        if (Util.canSendMessage(channel)) {
            channel.sendMessageEmbeds(Util.getDefEmbedWithFooter().appendDescription(content).build()).queue();
        }
    }

    /**
     * Send the message when command not found
     * @param channel channel to send message in
     */
    public void sendInvalidCommandMessage(TextChannel channel, String prefix, String command) {
        if (Util.canSendMessage(channel)) {
            sendMessage(Bot.getProperty("invalid-command").replaceAll("\\{prefix}",prefix).replaceAll("\\{command}", command)
              , Bot.getProperty("errorColour"), channel);
        }
    }

    public void sendCommandUsageMessage(Command command, TextChannel channel, String prefix) {
        if (Util.canSendMessage(channel)) {
            channel.sendMessageEmbeds(Util.getDefEmbedWithFooter(Bot.getProperty("errorColour")).appendDescription("Please follow the correct usage `" +
              prefix + command.getName() + " " + command.getArguments() + "`").build()).queue();
        }
    }

    /**
     * Send the default message that you don't have permission for
     * @param channel The channel to send the message in
     */
    public void sendNoPermissionMessage(TextChannel channel, String prefix, String command) {
        if (Util.canSendMessage(channel)) {
            sendMessage(Bot.getProperty("no-permission").replaceAll("\\{prefix}",prefix).replaceAll("\\{command}", command)
              , Bot.getProperty("errorColour"), channel);
        }
    }


    /**
     * Get a list of permissions a user has
     * @param member the member
     * @return the list of permissions
     */
    public List<String> getAllPerms(Member member) {
        if (member.isOwner()) return Bot.allPerms;
        List<String> permissions = new ArrayList<>();
        List<Role> roles = new ArrayList<>(member.getRoles());
        roles.add(member.getGuild().getPublicRole());
        for (Role role : roles) {
            try {
                permissions.addAll(Bot.getDriver().getPerms(role.getId()));
                if (permissions.contains("*")) return Bot.allPerms;
            } catch (ResultNotFoundException e) {
                logger.error("Failed permission check. Look into this");
            }
        }
        return permissions;
    }

}
