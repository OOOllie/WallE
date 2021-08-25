package best.ollie.walle;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.commands.HelpCommand;
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

public class Bot {

    @Getter
    public static JDA bot;
    @Getter
    public final static Driver driver = new Driver();
    public static Logger logger = LogManager.getLogger(Bot.class);
    @Getter
    public final static Properties property = new Properties();

    public static void main(String[] args) {
        System.out.println(logger.getLevel());
        try {
            //Load the config file
            FileInputStream stream = new FileInputStream("config.txt");
            property.load(stream);
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

    private static void registerEvents(JDA bot) {
        bot.addEventListener(new CommandHandler());
    }

    private static void registerCommands() {
        CommandHandler.getInstance().registerCommand(new HelpCommand());
    }
}
