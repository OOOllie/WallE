package best.ollie.walle.commands;

import best.ollie.walle.Bot;
import best.ollie.walle.exceptions.ResultNotFoundException;
import best.ollie.walle.util.Util;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
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
    public boolean hasPerm(Member member, String permission) {
        //Grab a list of roles the user has and add the everyone role as that is shared with everyone
        List<Role> roles = new ArrayList<>(member.getRoles());
        roles.add(member.getGuild().getPublicRole());
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
            if (!Util.canSendMessage(eventNew.getChannel())) { return; }
            Guild guild = eventNew.getGuild();
            Member member = eventNew.getMember();
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
                    if (!hasPerm(member, commandGroup.getPermission())) {
                        sendNoPermissionMessage(eventNew.getChannel());
                        return;
                    }
                    //Otherwise remove the command to grab the list of arguments
                    String[] args = getArguments(message, commandGroup.getName());
                    commandGroup.run(eventNew, args);
                    Bot.logger.info("Command '" + commandGroup.getName() + "' executed by " + eventNew.getAuthor().getName() + " With args: " + getArgumentsLine(args));
                    return;
                }
            }

            //If the message was a general command, repeat the same process above.
            for (Command command : commands) {
                if (message.split(" ")[0].equals(command.getName())) {
                    if (!hasPerm(member, command.getPermission())) {
                        sendNoPermissionMessage(eventNew.getChannel());
                        return;
                    }
                    String[] args = getArguments(message, command.getName());
                    command.run(eventNew, args);
                    Bot.logger.info("Command '" + command.getName() + "' executed by " + eventNew.getAuthor().getName() + " With args: " + getArgumentsLine(args));
                    return;
                }
            }
            //If command was not found, tell them the help message
            eventNew.getChannel().sendMessageEmbeds(Util.getDefEmbedWithFooter().appendDescription("**Invalid command!** " + prefix + "help for help.").build());
        }
    }

    /**
     * Send the default message that you don't have permission for
     * @param channel The channel to send the message in
     */
    private void sendNoPermissionMessage(TextChannel channel) {
        channel.sendMessageEmbeds(Util.getDefEmbedWithFooter().setDescription("You don't have permission for this command").build()).queue();
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
            armsg.append(s);
        }
        return armsg.toString();
    }

    /**
     * @param member the member to see if they have permission
     * @return A list of commands in no group that a specific user has access to
     */
    public List<Command> getGeneralCommands(Member member) {
        Bot.logger.info("Getting all commands the user can access not in a group: " + member.getNickname());
        List<Command> accessCommands = new ArrayList<>();
        for (Command command : commands) {
            if (hasPerm(member, command.getPermission())) {
                accessCommands.add(command);
            }
        }
        return accessCommands;
    }

    /**
     * @param member the member to see if they have permission
     * @return A list of commands in the group that the member can access
     */
    public List<Command> getGroupCommands(Member member, CommandGroup group) {
        Bot.logger.info("Getting commands user: " + member.getNickname() + " can access in: " + group.getName());
        List<Command> accessCommands = new ArrayList<>();
        //Get a list of all command group commands they can access within that group
        for (Command command : group.getCommands()) {
            if (CommandHandler.getInstance().hasPerm(member, command.getPermission())) {
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
        Bot.logger.info("Getting command categories");
        HashMap<String, List<Command>> categories = new HashMap<>();
        //Create a general category for commands without a group
        categories.put("General", getGeneralCommands(member));

        //For every command the user has permission for, add it to the general list

        //For every command group command, add them as a list
        for (CommandGroup commandGroup : groups) {
            //If the user can access the command group
            if (CommandHandler.getInstance().hasPerm(member, commandGroup.getPermission())) {
                List<Command> accessCommands = getGroupCommands(member, commandGroup);
                //Make sure default command is added
                accessCommands.add(commandGroup);
                categories.put(commandGroup.getName().substring(0,1).toUpperCase() + commandGroup.getName().substring(1).toLowerCase(), accessCommands);
            }
        }
        return categories;
    }



}
