package best.ollie.walle.events;

import best.ollie.walle.Bot;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Handle database connections to ensure the database is up to date
 */
public class OnJoinEventListener extends ListenerAdapter {

	/**
	 * Register a guild in the database
	 * @param event the event for the join
	 */
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		Bot.driver.setup(event.getGuild().getId());
		Bot.logger.info("Registering guild: " + event.getGuild().getId());
	}

}
