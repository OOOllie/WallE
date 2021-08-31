package best.ollie.walle.events;

import best.ollie.walle.Bot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handle database connections to ensure the database is up to date
 */
public class OnJoinEventListener extends ListenerAdapter {

	/**
	 * Store the logger object
	 */
	private final Logger logger = LogManager.getLogger(OnJoinEventListener.class);

	/**
	 * Register a guild in the database
	 * @param event the event for the join
	 */
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		Bot.getDriver().setup(event.getGuild().getId());
		logger.info("Registering guild: " + event.getGuild().getId());
	}

}
