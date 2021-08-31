package best.ollie.walle.events;

import best.ollie.walle.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle removing settings from database when bot leaves
 */
public class OnLeaveEventListener extends ListenerAdapter {

	/**
	 * Store the logger object
	 */
	private final Logger logger = LogManager.getLogger(OnLeaveEventListener.class);

	/**
	 * Remove everything from database when we leave
	 * @param event The event
	 */
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		Guild guild = event.getGuild();
		Bot.getDriver().removeGuild(guild.getId());
		List<Role> roles = new ArrayList<>(guild.getRoles());
		roles.add(guild.getPublicRole());
		Bot.getDriver().resetPermissions(roles);
		logger.info("Removing guild: " + guild.getId());
	}

}
