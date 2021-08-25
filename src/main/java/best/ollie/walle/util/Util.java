package best.ollie.walle.util;

import best.ollie.walle.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.awt.*;

/**
 * Class to provide some helper functions for the bot
 */
public class Util {

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
    return new EmbedBuilder().setColor(Color.decode(Bot.getProperty("embedColour"))).setFooter(Bot.getProperty("footerText"));
  }

  /**
   * Check whether bot can send message in a channel
   * @param channel the channel to check
   * @return true if message can be sent, false if cannot
   */
  public static boolean canSendMessage(TextChannel channel) {
    Member member = channel.getGuild().getMemberById(Bot.bot.getSelfUser().getId());
    return PermissionUtil.checkPermission(channel, member, Permission.MESSAGE_WRITE);
  }

}
