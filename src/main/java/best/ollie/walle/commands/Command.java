package best.ollie.walle.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Class for a basic command so we the command handler can grab what it needs
 * Abstract as is not an actual command to ever be instantiated and ran
 */
public abstract class Command {

    private final String name;
    private final String arguments;
    private final String description;
    private final String permission;

    public Command(String name, String arguments, String description, String permission) {
        this.name = name;
        this.arguments = arguments;
        this.description = description;
        this.permission = permission;
    }

    public abstract void run(GuildMessageReceivedEvent event, String[] args, String prefix);

    public String getName() {
        return this.name;
    }

    public String getArguments() { return this.arguments; }

    public String getDescription() { return this.description; }

    public String getPermission() { return this.permission; }


}