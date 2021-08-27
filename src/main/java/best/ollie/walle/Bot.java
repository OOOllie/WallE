package best.ollie.walle;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.commands.HelpCommand;
import best.ollie.walle.commands.permissions.PermissionGroup;
import best.ollie.walle.commands.permissions.PermissionsAddCommand;
import best.ollie.walle.commands.permissions.PermissionsListCommand;
import best.ollie.walle.events.OnJoinEventListener;
import best.ollie.walle.events.OnLeaveEventListener;
import best.ollie.walle.util.Driver;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Main bot class to load the bot and properties, load the commands events before we can start
 */
public class Bot {

    /**
     * Stores the JDA instace of the bot
     */
    @Getter
    public static JDA bot;

    /**
     * Stores the drive to handle database connections
     */
    @Getter
    public final static Driver driver = new Driver();

    /**
     * Store the console logger for the bot
     */
    public static final Logger logger = LogManager.getLogger(Bot.class);

    /**
     * Stores the config file property
     */
    @Getter
    private final static Properties property = new Properties();

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
    }

    /**
     * Register the events of the bot
     * @param bot the bot user
     */
    private static void registerEvents(JDA bot) {
        bot.addEventListener(new CommandHandler());
        bot.addEventListener(new OnJoinEventListener());
        bot.addEventListener(new OnLeaveEventListener());
    }

    /**
     * Add all the commands for the command handler
     */
    private static void registerCommands() {
        CommandHandler.getInstance().registerCommand(new HelpCommand());
        PermissionGroup permsGroup = new PermissionGroup();
        permsGroup.registerCommand(new PermissionsAddCommand());
        permsGroup.registerCommand(new PermissionsListCommand());
        CommandHandler.getInstance().registerGroup(permsGroup);
    }

    /**
     * @param prop What to look for in the config file
     * @return The property in the config file
     */
    public static String getProperty(String prop) {
        return property.getProperty(prop);
    }
}
