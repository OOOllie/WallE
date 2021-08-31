package best.ollie.walle.events;

import best.ollie.walle.Bot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to reset music if bot is disconnected
 */
public class OnBotDisconnectListener extends ListenerAdapter {

	private Logger logger = LogManager.getLogger(OnBotDisconnectListener.class);

	/**
	 * What to do when bot leaves a voice channel
	 * @param event The event
	 */
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		if (event.getEntity().getUser().equals(Bot.getBot().getSelfUser())) {
			Bot.getManager().getGuildAudioPlayer(event.getGuild()).scheduler.stop();
		}
	}

}
