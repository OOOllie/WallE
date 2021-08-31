package best.ollie.walle.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

/**
 * Class to handle the queue for each guild
 */
public class TrackScheduler extends AudioEventAdapter {

	/**
	 * Store the logger for the class
	 */
	private final Logger logger = LogManager.getLogger(TrackScheduler.class);

	/**
	 * Store the queue
	 */
	private final LinkedList<AudioTrack> queue = new LinkedList<>();

	/**
	 * Store the audio player
	 */
	private final AudioPlayer player;

	/**
	 * Store the guild
	 */
	private final Guild guild;

	/**
	 * @param player The audio player for the scheduler
	 * @param guild The guild the scheduler is running in
	 */
	public TrackScheduler(AudioPlayer player, Guild guild) {
		this.player = player;
		this.guild = guild;
	}

	/**
	 * When a track ends
	 * @param player the player that has finished a track
	 * @param track The track that has ended
	 * @param endReason Why it ended
	 */
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			if (queue.size() > 0) startNextTrack();
			else guild.getAudioManager().closeAudioConnection();
		}

	}

	/**
	 * If the player errors
	 * @param player the player that has errored on a track
	 * @param track The track that has errored
	 * @param exception What happened
	 */
	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		logger.error("Unknown music error");
		exception.printStackTrace();
		startNextTrack();
	}

	/**
	 * If the player  gets stuck
	 * @param player the player that has gotten stuck
	 * @param track The track that is stuck
	 * @param thresholdMs How long it waited for
	 */
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		logger.error("Track " + track.getIdentifier() + " failed to load.");
		startNextTrack();
	}

	/**
	 * Queue a song
	 * @param track The track to queue
	 */
	public void queue(AudioTrack track) {
		if (!player.startTrack(track, true)) {
			queue.offer(track);
		}
	}

	/**
	 * Priority add a song
	 * @param track The track to add
	 * @return true if the track was started, false if it is waiting to play
	 */
	public boolean queueFirst(AudioTrack track) {
		boolean started = player.startTrack(track,true);
		if (!started) {
			queue.addFirst(track);
		}
		return started;
	}

	/**
	 * Start the next song in the queue
	 */
	private void startNextTrack() {
		player.startTrack(queue.poll(), false);
	}

	/**
	 * Skip a track
	 */
	public void skip() {
		boolean leaveVoice = queue.size() == 0;
		startNextTrack();
		if (leaveVoice) guild.getAudioManager().closeAudioConnection();
	}

	/**
	 * @return The current queue
	 */
	public LinkedList<AudioTrack> getQueue() {
		return queue;
	}

	/**
	 * Remove a song at an index
	 * @param index The index of the song in the queue
	 */
	public void remove(int index) {
		queue.remove(index);
	}

	/**
	 * Stop and leave the voice channel
	 */
	public void stop() {
		guild.getAudioManager().closeAudioConnection();
		queue.clear();
	}

	/**
	 * Pause the player
	 */
	public void pause() {
		player.setPaused(!player.isPaused());
	}

	/**
	 * @return Whether the player is paused
	 */
	public boolean isPaused() {
		return player.isPaused();
	}

	/**
	 * Set the volume
	 * @param volume the volume
	 */
	public void setVolume(int volume) {
		player.setVolume(volume);
	}

	/**
	 * @return The track being played, could be null
	 */
	public AudioTrack getCurrentTrack() {
		return player.getPlayingTrack();
	}

}
