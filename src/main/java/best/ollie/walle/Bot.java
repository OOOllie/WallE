package best.ollie.walle;

import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandGroup;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.commands.HelpCommand;
import best.ollie.walle.commands.music.*;
import best.ollie.walle.commands.permissions.*;
import best.ollie.walle.commands.setup.PrefixCommand;
import best.ollie.walle.commands.setup.SetupGroup;
import best.ollie.walle.events.OnBotDisconnectListener;
import best.ollie.walle.events.OnJoinEventListener;
import best.ollie.walle.events.OnLeaveEventListener;
import best.ollie.walle.util.Driver;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.collections4.list.TreeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Main bot class to load the bot and properties, load the commands events before we can start
 */
public class Bot {

    /**
     * Stores the JDA instance of the bot
     */
    private static JDA bot;

    /**
     * Stores the drive to handle database connections
     */
    private final static Driver driver = new Driver();

    /**
     * Store the console logger for the bot
     */
    private static final Logger logger = LogManager.getLogger(Bot.class);

    /**
     * Stores the config file property
     */
    private final static Properties property = new Properties();

    /**
     * Stores the list of permissions for all commands
     */
    public static List<String> allPerms = new TreeList<>();

    /**
     * Store the MusicManager for the bot
     */
    private static final MusicManager manager = new MusicManager();

    public static void main(String[] args) {
        try {
            //Load the config file
            FileInputStream stream = new FileInputStream("config.txt");
            property.load(stream);
            //Load the bot from token
            bot = JDABuilder.createDefault(property.getProperty("token")).build();
            bot.awaitReady();
        } catch (FileNotFoundException exception) {
            logger.error("Config file not found, please create a file called \"config.txt\" in the same location as the jar!");
            return;
        } catch (IOException exception) {
            logger.error("Failed to load config file, error below: ");
            exception.printStackTrace();
            return;
        } catch (LoginException exception) {
            logger.error("Failed to load bot token, please ensure valid token is provided.");
            return;
        } catch (InterruptedException exception) {
            logger.error("Bot failed to initialise, was interrupted: ");
            exception.printStackTrace();
            return;
        }
        logger.info("Bot successfully logged in");

        registerEvents(bot);
        logger.info("Registered events");
        registerCommands();
        logger.info("Registered commands.");

        //Initialise Database
        driver.setUrl(property.getProperty("databaseAddress"),property.getProperty("databaseName"));
        driver.setPassword(property.getProperty("databasePassword"));
        driver.setUsername(property.getProperty("databaseUsername"));
        try {
            driver.initialise();
        } catch (SQLException exception) {
            logger.error("Failed to connect to database, error below: ");
            exception.printStackTrace();
            return;
        }
        logger.info("Database connection established.");

        //Initialise the all permissions array
        allPerms.add("*");
        for (Command command : CommandHandler.getInstance().getCommands()) {
            allPerms.add(command.getPermission());
        }
        for (CommandGroup group : CommandHandler.getInstance().getGroups()) {
            allPerms.add(group.getPermission());
            for (Command command : group.getCommands()) {
                allPerms.add(command.getPermission());
            }
        }
    }

    /**
     * Register the events of the bot
     * @param bot the bot user
     */
    private static void registerEvents(JDA bot) {
        bot.addEventListener(new CommandHandler());
        bot.addEventListener(new OnJoinEventListener());
        bot.addEventListener(new OnLeaveEventListener());
        bot.addEventListener(new OnBotDisconnectListener());
    }

    /**
     * Add all the commands for the command handler
     */
    private static void registerCommands() {
        CommandHandler.getInstance().registerCommand(new HelpCommand());
        PermissionGroup permsGroup = new PermissionGroup();
        permsGroup.registerCommand(new PermissionsAddCommand());
        permsGroup.registerCommand(new PermissionsListCommand());
        permsGroup.registerCommand(new PermissionsRemoveCommand());
        permsGroup.registerCommand(new PermissionsResetCommand());
        CommandHandler.getInstance().registerGroup(permsGroup);
        SetupGroup setupGroup = new SetupGroup();
        setupGroup.registerCommand(new PrefixCommand());
        CommandHandler.getInstance().registerGroup(setupGroup);
        MusicGroup musicGroup = new MusicGroup();
        musicGroup.registerCommand(new MusicAddCommand());
        musicGroup.registerCommand(new MusicSkipCommand());
        musicGroup.registerCommand(new MusicQueueCommand());
        musicGroup.registerCommand(new MusicStopCommand());
        musicGroup.registerCommand(new MusicPauseCommand());
        musicGroup.registerCommand(new MusicVolumeCommand());
        musicGroup.registerCommand(new MusicPlayCommand());
        musicGroup.registerCommand(new MusicRemoveCommand());
        CommandHandler.getInstance().registerGroup(musicGroup);
    }

    /**
     * @param prop What to look for in the config file
     * @return The property in the config file
     */
    public static String getProperty(String prop) {
        String content = property.getProperty(prop);
        if (content == null) return "Please add " + prop + " to the config.txt file";
        else return content;
    }

    /**
     * @return The discord bot object
     */
    public static JDA getBot() {
        return bot;
    }

    /**
     * @return The driver object for database management
     */
    public static Driver getDriver() {
        return driver;
    }

    /**
     * @return The music manager object
     */
    public static MusicManager getManager() {
        return manager;
    }
}
