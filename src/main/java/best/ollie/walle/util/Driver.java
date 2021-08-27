package best.ollie.walle.util;

import best.ollie.walle.exceptions.ResultNotFoundException;
import net.dv8tion.jda.api.entities.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle all SQL connections for the bot
 */
public class Driver {

  /**
   * Store the connection the database
   */
  private Connection con;

  /**
   *   Store the url connection to the database (will be updated to contain address and name from config)
   */
  private String url = "jdbc:mysql://ADDRESS/NAME?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

  /**
   * Store the username from the database
   */
  private String username;

  /**
   * Store the password to the database
   */
  private String password;

  /**
   * Store the logger to handle errors from the database
   */
  private final Logger logger = LogManager.getLogger(Driver.class);

  /**
   * Initialise the connection to the database
   * @throws SQLException Connection could not be created due to invalid/blocked connection
   */
  public void initialise() throws SQLException{
      con = DriverManager.getConnection(url, username, password);
  }

  /**
   * Update the URL to initialise the connection with
   * @param address address to the database
   * @param name name of the database
   */
  public void setUrl(String address, String name) {
    url = url.replace("ADDRESS",address).replace("NAME",name);
  }

  /**
   * Set the username of the user to connect to the database
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Set the password of the suer to connect to the database
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Initialise the database with settings for the guild
   * @param guildID the id of the guild to update
   */
  public void setup(String guildID) {
    logger.info("Initialising database settings for guildID: " + guildID);
    try {
      String query = "INSERT INTO guilds (guildID,nsfwRole,sanctionsRole,suggestionsChannel,prefix) VALUES (?,?,?,?,?)";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, guildID);
      st.setString(2, "");
      st.setString(3, "");
      st.setString(4, "");
      st.setString(5, "-");
      st.executeUpdate();
    } catch (Exception exc) {
      logger.error("Failed to execute SQL query for guildID: " + guildID);
      exc.printStackTrace();
    }
  }

  /**
   * Update the prefix of a specific guild
   * @param guildID guild to update
   * @param prefix new prefix
   */
  public void updatePrefix(String guildID, String prefix) {
    logger.info("Updating prefix for guildID: " + guildID);
    String updatePrefix = "UPDATE guilds SET prefix=? WHERE guildID=?";
    try {
      PreparedStatement st = con.prepareStatement(updatePrefix);
      st.setString(1, prefix);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (SQLException exc) {
      logger.error("Failed to execute SQL query for guildID: " + guildID);
      exc.printStackTrace();
    }
  }

  /**
   * Update the suggestions channel ID for a specific guild
   * @param guildID guild to update
   * @param channelID the channel id
   */
  public void updateSuggestionChannel(String guildID, String channelID) {
    logger.info("Updating suggestions for guildID: " + guildID);
    String updateSuggestions = "UPDATE guilds SET suggestionsChannel=? WHERE guildID=?";
    try {
      PreparedStatement st = con.prepareStatement(updateSuggestions);
      st.setString(1, channelID);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (SQLException exc) {
      logger.error("Failed to execute SQL query for guildID: " + guildID);
      exc.printStackTrace();
    }
  }

  /**
   * Update the nsfw role ID for a specific guild
   * @param guildID guild to update
   * @param roleID the role id
   */
  public void updateNSFWRole(String guildID, String roleID) {
    logger.info("Updating NSFW Role for guildID: " + guildID);
    String updateGuild = "UPDATE guilds SET nsfwRole=? WHERE guildID=?";
    try {
      PreparedStatement st = con.prepareStatement(updateGuild);
      st.setString(1, roleID);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (SQLException exc) {
      logger.error("Failed to execute SQL query to update NSFW role: " + guildID);
      exc.printStackTrace();
    }
  }

  /**
   * Update the sanctions role ID for a specific guild
   * @param guildID guild to update
   * @param roleID the role id
   */
  public void updateSanctionsRole(String guildID, String roleID) {
    logger.info("Updating sanctions role for guildID: " + guildID);
    String updateSanctions = "UPDATE guilds SET sanctionsRole=? WHERE guildID=?";
    try {
      PreparedStatement st = con.prepareStatement(updateSanctions);
      st.setString(1, roleID);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (SQLException exc) {
      logger.error("Failed to execute SQL query to update sanctions role: " + guildID);
      exc.printStackTrace();
    }
  }

  /**
   * Get the prefix of a specific guild
   * @param guildID the guild to get the prefix for
   * @return the guild id
   * @throws ResultNotFoundException thrown if no guild data is found (something bad happened)
   */
  public String getPrefix(String guildID) throws ResultNotFoundException {
    logger.info("Getting prefix for guildID: " + guildID);
    try {
      String getPrefix = "SELECT prefix FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(getPrefix);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      if (rs.next()) return rs.getString("prefix");
      else {
        logger.error("The guild does not exist in the database");
        throw new ResultNotFoundException("Guild does not exist in database, contact support: " + guildID);
      }
    } catch (Exception exc) {
      logger.error("Couldn't execute SQL statement: ");
      exc.printStackTrace();
      throw new ResultNotFoundException("SQL Query failed in database, please contact our support team.");
    }
  }

  /**
   * Get the NSFW role for a specific guild
   * @param guildID the guild to get the role for
   * @return the nsfw role id
   * @throws ResultNotFoundException thrown if no guild data is found (something bad happened)
   */
  public String getNSFWRole(String guildID) throws ResultNotFoundException{
    logger.info("Getting NSFW role for guildID: " + guildID);
    try {
      String getRole = "SELECT nsfwRole FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(getRole);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      if (rs.next()) return rs.getString("nsfwRole");
      else {
        logger.error("The guild does not exist in the database");
        throw new ResultNotFoundException("Guild does not exist in database, contact support: " + guildID);
      }
    } catch (Exception exc) {
      logger.error("Couldn't execute SQL statement: ");
      exc.printStackTrace();
      throw new ResultNotFoundException("SQL Query failed in database, please contact our support team.");
    }
  }

  /**
   * Get the sanctions role for a specific guild
   * @param guildID the guild to get the role for
   * @return the sanctions role id
   * @throws ResultNotFoundException thrown if no guild data is found (something bad happened)
   */
  public String getSanctionsRole(String guildID) throws ResultNotFoundException {
    logger.info("Getting sanctions role for guildID:" + guildID);
    try {
      String getUser = "SELECT sanctionsRole FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(getUser);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      if (rs.next()) return rs.getString("sanctionsRole");
      else {
        logger.error("The guild does not exist in the database");
        throw new ResultNotFoundException("Guild does not exist in database, contact support: " + guildID);
      }
    } catch (SQLException exc) {
      logger.error("Couldn't execute SQL statement: ");
      exc.printStackTrace();
      throw new ResultNotFoundException("SQL Query failed in database, please contact our support team.");
    }
  }

  /**
   * Get the NSFW role for a specific guild
   * @param guildID the guild to get the role for
   * @return the suggestions channel id
   * @throws ResultNotFoundException thrown if no guild data is found (something bad happened)
   */
  public String getSuggestionsChannel(String guildID) throws ResultNotFoundException {
    logger.info("Getting suggestions channel for guildID:" + guildID);
    try {
      String createUser = "SELECT suggestionsChannel FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      if (rs.next()) return rs.getString("suggestionsChannel");
      else {
        logger.error("The guild does not exist in the database");
        throw new ResultNotFoundException("Guild does not exist in database, contact support: " + guildID);
      }
    } catch (Exception exc) {
      logger.error("Couldn't execute SQL statement: ");
      exc.printStackTrace();
      throw new ResultNotFoundException("SQL Query failed in database, please contact our support team.");
    }
  }

  /**
   * Update the time for a specifc poll
   * @param pollID the id for a poll
   * @param time how long is left
   */
  public void updateTime(int pollID, long time) {
    logger.info("Updating time for: " + pollID + " to: " + time);
    try {
      String createUser = "UPDATE polls SET time=? WHERE pollID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setLong(1, time);
      st.setInt(2, pollID);
      st.executeUpdate(); // Execute the statement
    } catch (Exception exc) {
      logger.error("SQL Query failed in database, please contact our support team.");
      exc.printStackTrace();
    }
  }

  /**
   * Register a new poll
   * @param guildID The guild the poll is in
   * @param title The title of the poll
   * @param channelID Where the poll is
   * @param time How long the poll should last for
   * @param messageID The message to update
   * @return The generated poll ID and -1 if failed
   */
  public int addPoll(String guildID, String title, String channelID, long time, String messageID) {
    logger.info("Registering now poll.");
    try {
      String query = "INSERT INTO polls (guildID,title,channelID,time,messageID) VALUES (?,?,?,?,?)";
      PreparedStatement st = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
      st.setString(1, guildID);
      st.setString(2, title);
      st.setString(3,channelID);
      st.setLong(4,time);
      st.setString(5,messageID);
      st.executeUpdate();
      ResultSet rs = st.getGeneratedKeys();
      return rs.getInt(1);
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support team");
      exc.printStackTrace();
    }
    return -1;
  }

  /**
   * Used to remove a poll once it has finished
   * @param pollID the id of the poll to be removed
   */
  public void removePoll(String pollID) {
    logger.info("Ending poll: " + pollID);
    try {
      String query = "DELETE FROM polls WHERE pollID=?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setString(1, pollID); // Insert using function to prevent SQL injection
      st.executeUpdate(); // Execute query
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support team.");
      exc.printStackTrace();
    }
  }

  /*
  public List<Poll> getPolls(String guildID) {
    try {
      String createUser = "SELECT * FROM polls WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      List<Poll> polls = new ArrayList<Poll>();
      while (rs.next()) {
        String guild = rs.getString(2);
        int id = rs.getInt(1);
        String title = rs.getString(3);
        String channelID = rs.getString(4);
        long time = Integer.toUnsignedLong(rs.getInt(5));
        String messageID = rs.getString(6);
        polls.add(new Poll(id,title,time,getOptions(id),channelID,guildID,messageID));
      }
      return polls;
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return new ArrayList<Poll>();
  }*/

  /**
   * Store the options to vote for a poll
   * @param pollID The id of the poll
   * @param options The list of options
   */
  public void setOptions(int pollID,List<String> options) {
    logger.info("Setting options for: " + pollID);
    try {
      for (String option : options) {
        String setOption = "INSERT INTO options (pollID,optionString) VALUES (?,?)";
        PreparedStatement st = con.prepareStatement(setOption);
        st.setInt(1, pollID);
        st.setString(2, option);
        st.executeUpdate();
      }
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support.");
      exc.printStackTrace();
    }
  }

  /**
   * Get the list of options for a poll
   * @param pollID the poll to get the options for
   * @return the options
   * @throws ResultNotFoundException Happens when no options are found
   */
  public List<String> getOptions(int pollID) throws ResultNotFoundException {
    logger.info("Getting options for: " + pollID);
    try {
      String createUser = "SELECT optionString FROM options WHERE pollID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setInt(1, pollID);
      ResultSet rs = st.executeQuery();
      List<String> options = new ArrayList<>();
      while (rs.next()) {
        options.add(rs.getString(1));
      }
      return options;
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
      throw new ResultNotFoundException("No options found for poll: " + pollID);
    }
  }

  /**
   * Delete the options for a poll once it has finished
   * @param pollID poll to delete for
   */
  public void deleteOptions(int pollID) {
    logger.info("Deleting options for poll: " + pollID);
    try {
      String query = "DELETE FROM options WHERE pollID = ?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setInt(1, pollID); // Insert using function to prevent SQL injection
      st.executeUpdate(); // Execute query
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
    }
  }

  /**
   * Add a permission for a role
   * @param roleID the role id to add a permission for
   * @param permission the permission to add
   */
  public void addPerm(String roleID, String permission) {
    logger.info("Adding permission: " + permission + " for role: " + roleID);
    try {
      String query = "INSERT INTO permissions (roleID,permission) VALUES (?,?)";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, roleID);
      st.setString(2, permission);
      st.executeUpdate();
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
    }
  }

  /**
   * Remove all permissions from a guild
   * @param roles The list of roles to remove from the database
   */
  public void resetPermissions(List<Role> roles) {
    logger.info("Removing all roles from guild");
    for (Role role : roles) {
      removePermRole(role.getId());
    }
  }

  /**
   * Remove a permission from a role
   * @param roleID role id to remove it from
   * @param permission the permission to remove
   */
  public void removePerm(String roleID, String permission) {
    logger.info("Removing permission: " + permission + " from: " + roleID);
    try {
      String query = "DELETE FROM permissions WHERE roleID = ?  AND permission = ?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setString(1, roleID); // Insert using function to prevent SQL injection
      st.setString(2, permission);
      st.executeUpdate(); // Execute query
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
    }
  }

  /**
   * Remove all permissions for a role
   * @param roleID role id to remove it from
   */
  public void removePermRole(String roleID) {
    logger.info("Removing all permissions from: " + roleID);
    try {
      String query = "DELETE FROM permissions WHERE roleID = ?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setString(1, roleID); // Insert using function to prevent SQL injection
      st.executeUpdate(); // Execute query
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
    }
  }

  /**
   * @param guildID guild to remove
   */
  public void removeGuild(String guildID) {
    logger.info("Deleting guild: " + guildID);
    try {
      String query = "DELETE FROM guilds WHERE guildID = ?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setString(1, guildID); // Insert using function to prevent SQL injection
      st.executeUpdate(); // Execute query
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
    }
  }

  /**
   * Get the list of permissions for a role
   * @param roleID The role id to get permissions for
   * @return The list of permissions
   * @throws ResultNotFoundException thrown when database query fails
   */
  public List<String> getPerms(String roleID) throws ResultNotFoundException {
    logger.info("Getting permissions for: " + roleID);
    try {
      String getPerms = "SELECT permission FROM permissions WHERE roleID=?";
      PreparedStatement st = con.prepareStatement(getPerms);
      st.setString(1, roleID);
      ResultSet rs = st.executeQuery();
      List<String> perms = new ArrayList<>();
      while (rs.next()) {
        perms.add(rs.getString("permission"));
      }
      return perms;
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
      throw new ResultNotFoundException("Failed to get permissions for: " + roleID);
    }
  }

  /**
   * Check if a role ID has a specific permission
   * @param roleID the role ID to check for
   * @param permission the permission to check
   * @return true if they have it, false if they don't
   */
  public boolean checkPerm(String roleID, String permission) {
    logger.info("Checking if: " + roleID + " has permission: " + permission);
    try {
      String checkPerm = "SELECT permission FROM permissions WHERE roleID=? AND permission=?";
      PreparedStatement st = con.prepareStatement(checkPerm);
      st.setString(1, roleID);
      st.setString(2,permission);
      ResultSet rs = st.executeQuery();
      rs.last();
      return rs.getRow() > 0;
    } catch (SQLException exc) {
      logger.error("SQL query failed in database, please contact support");
      exc.printStackTrace();
      return false;
    }
  }

  /**
   * Close the connection to the database
   */
  public void close() {
    try {
      con.close();
    } catch (SQLException exc) {
      logger.error("Failed to close connection.");
      exc.printStackTrace();
    }
  }
}
