package best.ollie.walle.util;

import best.ollie.walle.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

/**
 * Class to provide some helper functions for the bot
 */
public class Util {

  /**
   * Store the logger object
   */
  private static final Logger logger = LogManager.getLogger(Util.class);

  /**
   * @return A basic embed message with no footer
   */
  public static EmbedBuilder getDefEmbed() {
    return new EmbedBuilder().setColor(Color.decode(Bot.getProperty("embedColour")));
  }

  /**
   * @return A basic embed message with footer
   */
  public static EmbedBuilder getDefEmbedWithFooter() {
    return new EmbedBuilder().setColor(Color.decode(Bot.getProperty("embedColour"))).setFooter(Bot.getProperty("footerText"), Bot.getBot().getSelfUser().getAvatarUrl());
  }

  public static EmbedBuilder getDefEmbedWithFooter(String colour) {
    if (colour.matches("^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")) return new EmbedBuilder().setColor(Color.decode(colour))
      .setFooter(Bot.getProperty("footerText"), Bot.getBot().getSelfUser().getAvatarUrl());
    else {
      logger.error("Failed to load a colour provided, please ensure all config options are in the format of #XXXXX");
      return new EmbedBuilder().setFooter(Bot.getProperty("footerText"),  Bot.getBot().getSelfUser().getAvatarUrl());
    }
  }

  /**
   * Check whether bot can send message in a channel
   * @param channel the channel to check
   * @return true if message can be sent, false if cannot
   */
  public static boolean canSendMessage(TextChannel channel) {
    Member member = channel.getGuild().getMemberById(Bot.getBot().getSelfUser().getId());
    logger.info("Checking if we can send message in: " + channel.getId());
    return PermissionUtil.checkPermission(channel, member, Permission.MESSAGE_WRITE);
  }

  /**
   * @param roleId The role id
   * @param guild the guild the role should be in
   * @return the role object or null if role isn't found
   */
  public static Role convertStringToRole(String roleId, Guild guild) {
    //Special case if they want to add the role for everyone
    if (roleId.equals("@everyone")) return guild.getPublicRole();

    //Need to remove the non number characters
    if (roleId.startsWith("<@&") && roleId.endsWith(">")) {
      roleId = roleId.replaceAll("<@&", "").replaceAll(">","");
    }

    //If the final ID is not a number then return null
    if (!roleId.matches("^[0-9]*$")) return null;

    //Get discord to convert the id to a role
    try {
      return guild.getRoleById(roleId);
    } catch (NumberFormatException exception) {
      //If they haven't provided a valid integer id (basically it's too large, return nothing
      return null;
    }
  }

  /**
   * @param seconds Number of seconds
   * @return The time string in minutes seconds and hours
   */
  public static String convertSecondsToHMmSs(long seconds) {
    long s = seconds % 60;
    long m = (seconds / 60) % 60;
    long h = (seconds / (60 * 60)) % 24;
    return String.format("%02d:%02d:%02d", h,m,s);
  }

}
