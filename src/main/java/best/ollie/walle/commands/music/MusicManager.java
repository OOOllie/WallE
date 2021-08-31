package best.ollie.walle.commands.music;

import best.ollie.walle.Bot;
import best.ollie.walle.commands.Command;
import best.ollie.walle.commands.CommandHandler;
import best.ollie.walle.util.Util;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Store the useful things for handling music commands
 */
public class MusicManager {

	/**
	 * Store the logger object
	 */
	private final Logger logger = LogManager.getLogger(MusicManager.class);

	/**
	 * Store the global audio manager for the bot
	 */
	private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

	/**
	 * Store the map of managers for each guild
	 */
	private final Map<String, GuildMusicManager> musicManagers = new HashMap<>();

	/**
	 * Initialise the music sources
	 */
	public MusicManager() {
		AudioSourceManagers.registerRemoteSources(playerManager);
	}

	/**
	 * Get the audio player for a guild
	 * @param guild The guild to get the player for
	 * @return The audio player
	 */
	public synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		//Get the manager from the hash map
		GuildMusicManager musicManager = musicManagers.get(guild.getId());

		//If the guild doesn't have a manager create it
		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager, guild);
			musicManagers.put(guild.getId(), musicManager);
		}

		//Set the sending handler to be our audio handler class
		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
		return musicManager;
	}

	/**
	 * Queue the song
	 * @param channel The channel the command was run in
	 * @param trackUrl The search query
	 * @param interrupt Whether to play immediately or not
	 */
	public void loadAndPlay(final TextChannel channel, final String trackUrl, boolean interrupt) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		//Get the music manager to load the track
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				if (!interrupt) {
					sendTrackInfoMessage(channel, track.getInfo(),"Song Added");
					play(musicManager, track);
				} else {
					sendTrackInfoMessage(channel, track.getInfo(), "Song Playing Now");
					playNow(musicManager, track);
				}
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				//If a playlist is loaded, get the selected track or get the first in the list
				AudioTrack firstTrack = playlist.getSelectedTrack();
				if (firstTrack == null) {
					firstTrack = playlist.getTracks().get(0);
				}

				if (!interrupt) {
					sendTrackInfoMessage(channel, firstTrack.getInfo(), "Song Added");
					play(musicManager, firstTrack);
				} else {
					sendTrackInfoMessage(channel, firstTrack.getInfo(), "Song Playing Now");
					playNow(musicManager, firstTrack);
				}
			}

			@Override
			public void noMatches() {
				CommandHandler.getInstance().sendMessage(Bot.getProperty("music-not-found")
						.replaceAll("\\{search}", trackUrl)
					, Bot.getProperty("errorcolour"), channel);
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				channel.sendMessage("Could not play: " + exception.getMessage()).queue();
			}
		});
	}

	/**
	 * Play a song by adding the song to the queue
	 * @param musicManager The music manager
	 * @param track Track to queue
	 */
	private void play(GuildMusicManager musicManager, AudioTrack track) {
		musicManager.scheduler.queue(track);
	}

	/**
	 * Play a song immediately
	 * @param musicManager The manager to play it with
	 * @param track The track
	 */
	private void playNow(GuildMusicManager musicManager, AudioTrack track) {
		boolean started = musicManager.scheduler.queueFirst(track);
		//If it wasn't the first song, skip the currently playing song
		if (!started) {
			musicManager.scheduler.skip();
		}
	}

	/**
	 * Skip a song in the queue
	 * @param guild The guild  to skip song for
	 */
	public void skipTrack(Guild guild) {
			getGuildAudioPlayer(guild).scheduler.skip();
	}

	/**
	 * Check if we have permission to connect to a voice channel
	 * @param channel the channel to check permission for
	 * @return true if we can connect false otherwise
	 */
	public boolean canConnectToVoiceChannel(VoiceChannel channel) {
			Member member = channel.getGuild().getMember(Bot.getBot().getSelfUser());
			if (member == null) return false;
			return PermissionUtil.checkPermission(channel, member, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK);
	}

	/**
	 * Send a message containing the details of a song
	 * @param channel The channel
	 * @param info The track information object
	 * @param title The title of the message
	 */
	private void sendTrackInfoMessage(TextChannel channel, AudioTrackInfo info, String title) {
		if (Util.canSendMessage(channel)) {
			EmbedBuilder builder = Util.getDefEmbedWithFooter(Bot.getProperty("successColour"));
			builder.setTitle( title + ": " + info.title);
			builder.appendDescription("Length: " + Util.convertSecondsToHMmSs(info.length / 1000) + "\n");
			builder.appendDescription("Link: " + info.uri);
			channel.sendMessageEmbeds(builder.build()).queue();
		}
	}

	/**
	 * Check if the bot and a user are in the same channel
	 * @param member The member to check for
	 * @return True if they are
	 */
	public boolean inSameVoiceChannel(Member member) {
		if (!member.getVoiceState().inVoiceChannel() || !member.getGuild().getAudioManager().isConnected()) return true;
		return !member.getVoiceState().getChannel().equals(member.getGuild().getAudioManager().getConnectedChannel());
	}

	/**
	 * Run the command to add or play a song now
	 * @param args The arguments it was run with
	 * @param event The message event
	 * @param command The command that was ran
	 * @param prefix The prefix of the bot
	 * @param interrupt Whether to play the song now
	 */
	public void runCommand(String[] args, GuildMessageReceivedEvent event, Command command, String prefix, boolean interrupt) {
		if (args.length < 1) {
			CommandHandler.getInstance().sendCommandUsageMessage(command, event.getChannel(), prefix);
		} else if (args.length == 1 && args[0].startsWith("http")) {
			if (Bot.getManager().openAudioConnection(prefix, event)) {
				Bot.getManager().loadAndPlay(event.getChannel(), args[0], interrupt);
			}
		} else {
			if (Bot.getManager().openAudioConnection(prefix, event)) {
				StringBuilder builder = new StringBuilder();
				builder.append("ytsearch: ");
				for (String arg : args) {
					builder.append(arg).append(" ");
				}
				Bot.getManager().loadAndPlay(event.getChannel(), builder.toString(), interrupt);
			}
		}
	}

	/**
	 * Open an audio connection to a channel
	 * @param prefix The prefix of the bot
	 * @param event The event of the command that was run
	 * @return
	 */
	private boolean openAudioConnection(String prefix, GuildMessageReceivedEvent event) {
		//Check if we have reached max sice
		String maxSize = Bot.getProperty("max-queue-size");
		int max;
		try {
			max = Integer.parseInt(maxSize);
		} catch (NumberFormatException exception) {
			logger.error("Failed to read max queue size from config, make sure it is a number");
			return false;
		}

		if (Bot.getManager().getGuildAudioPlayer(event.getGuild()).scheduler.getQueue().size() == max) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("queue-reached-max")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{size}", String.valueOf(max))
				, Bot.getProperty("errorColour"), event.getChannel());
			return false;
		}

		//Check user is in a voice channel
		GuildVoiceState state = event.getMember().getVoiceState();
		if (!state.inVoiceChannel()) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("not-in-voice")
					.replaceAll("\\{prefix}", prefix)
				, Bot.getProperty("errorColour"), event.getChannel());
			return false;
		}

		//Check if we arent connect, that we can connect
		VoiceChannel channel = state.getChannel();
		AudioManager manager = event.getGuild().getAudioManager();
		if (!manager.isConnected()) {
			if (!Bot.getManager().canConnectToVoiceChannel(channel)) {
				CommandHandler.getInstance().sendMessage(Bot.getProperty("voice-no-perms")
						.replaceAll("\\{prefix}", prefix).replaceAll("\\{channel}", channel.getName())
					, Bot.getProperty("errorColour"), event.getChannel());
				return false;
			} else {
				manager.openAudioConnection(channel);
			}
		} else {
			//If we are connected, check we are in the same voice channel
			if (Bot.getManager().inSameVoiceChannel(event.getMember())) {
				CommandHandler.getInstance().sendMessage(Bot.getProperty("not-in-same-channel")
						.replaceAll("\\{prefix}", prefix).replaceAll("\\{channel}", manager.getConnectedChannel().getName())
					, Bot.getProperty("errorColour"), event.getChannel());
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the bot is connected and the member is in the same voice channel
	 * @param member The member to check their voice channel
	 * @param channel The channel where the command was run to reply
	 * @param prefix The prefix of the bot
	 * @return true if we command can continue
	 */
	public boolean handleChecks(Member member, TextChannel channel, String prefix) {
		AudioManager manager = member.getGuild().getAudioManager();
		if (!manager.isConnected()) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("voice-not-connected")
					.replaceAll("\\{prefix}", prefix)
				, Bot.getProperty("errorColour"), channel);
			return false;
		}

		if (Bot.getManager().inSameVoiceChannel(member)) {
			CommandHandler.getInstance().sendMessage(Bot.getProperty("not-in-same-channel")
					.replaceAll("\\{prefix}", prefix).replaceAll("\\{channel}", manager.getConnectedChannel().getName())
				, Bot.getProperty("errorColour"), channel);
			return false;
		}

		return true;
	}

}
