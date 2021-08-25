package best.ollie.walle.util;

import best.ollie.walle.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class Util {

  public static final String footerText = "WALL-E | Made by Ollie#3144";

  public static final int color = 0x008B8B;

  public static EmbedBuilder getDefEmbed() {
    return new EmbedBuilder().setColor(color);
  }

  public static EmbedBuilder getDefEmbedWithFooter() {
    return new EmbedBuilder().setColor(color).setFooter(footerText);
  }

  public static boolean canSendMessage(TextChannel channel) {
    Member member = channel.getGuild().getMemberById(Bot.bot.getSelfUser().getId());
    return PermissionUtil.checkPermission(channel, member, Permission.MESSAGE_WRITE);
  }

}
