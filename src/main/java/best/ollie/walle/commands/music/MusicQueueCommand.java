package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.util.Util;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Iterator;
import java.util.List;

/**
 * Command to see the music queue
 */
public class MusicQueueCommand extends Command {

	/**
	 * Initiate the command
	 */
	public MusicQueueCommand() {
		super("queue", "", "Get the current queue of songs", "music.queue");
	}

	/**
	 * @param event the event
	 * @param args the command arguments
	 * @param prefix the prefix of the bot
	 * @param permissions the permissions the command was run with
	 */
	@Override
	public void run(GuildMessageReceivedEvent event, String[] args, String prefix, List<String> permissions) {
		GuildMusicManager manager = Bot.getManager().getGuildAudioPlayer(event.getGuild());
		//Build the message
		EmbedBuilder eo = Util.getDefEmbedWithFooter();

		Iterator<AudioTrack> iterator = manager.scheduler.getQueue().iterator();
		StringBuilder sb = new StringBuilder();
		int count = 0;
		//For every category, create a new section inside the embved
		while (iterator.hasNext()) {
			AudioTrackInfo trackInfo = iterator.next().getInfo();
			sb.append("**").append(count+1).append("**").append(" - ").append(trackInfo.title).append(" - ")
				.append(Util.convertSecondsToHMmSs(trackInfo.length / 1000)).append("\n");
			count++;
		}
		if (count == 0) sb.append("No songs currently in queue");
		//Set the title
		eo.setTitle("Music Queue");
		//Set the message
		eo.appendDescription(sb.toString());
		//Send the message
		if (Util.canSendMessage(event.getChannel())) {
			event.getChannel().sendMessageEmbeds(eo.build()).queue();
		}
	}
}
