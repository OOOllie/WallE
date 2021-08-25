package best.ollie.walle.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class Command { // Abstract so it can't be instantiated, but it has to be extended

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

    public abstract void run(GuildMessageReceivedEvent event, String[] args);

    public String getName() {
        return this.name;
    }

    public String getArguments() { return this.arguments; }

    public String getDescription() { return this.description; }

    public String getPermission() { return this.permission; }


}