package best.ollie.walle.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Driver {

  private Connection con;
  private String url = "jdbc:mysql://ADDRESS/NAME?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
  private String username;
  private String password;

  public void initialise() throws SQLException{
      con = DriverManager.getConnection(url, username, password);
  }

  public void setUrl(String address, String name) {
    url = url.replace("ADDRESS",address).replace("NAME",name);
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setup(String guildID) {
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
      exc.printStackTrace();
    }
  }

  public void updatePrefix(String guildID, String prefix) {
    try {
      String createUser = "UPDATE guilds SET prefix=? WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, prefix);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void updateSuggestionChannel(String guildID, String channelID) {
    try {
      String createUser = "UPDATE guilds SET suggestionsChannel=? WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, channelID);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void updateNSFWRole(String guildID, String roleID) {
    try {
      String createUser = "UPDATE guilds SET nsfwRole=? WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, roleID);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void updateSanctionsRole(String guildID, String roleID) {
    try {
      String createUser = "UPDATE guilds SET sanctionsRole=? WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, roleID);
      st.setString(2, guildID);
      st.executeUpdate(); // Execute the statement
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public String getPrefix(String guildID) {
    try {
      String createUser = "SELECT prefix FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      return rs.getString("prefix");
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return "";
  }

  public String getNSFWRole(String guildID) {
    try {
      String createUser = "SELECT nsfwRole FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      return rs.getString("nsfwRole");
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return "";
  }

  public String getSanctionsRole(String guildID) {
    try {
      String createUser = "SELECT sanctionsRole FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      return rs.getString("sanctionsRole");
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return "";
  }

  public String getSuggestionsChannel(String guildID) {
    try {
      String createUser = "SELECT suggestionsChannel FROM guilds WHERE guildID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, guildID);
      ResultSet rs = st.executeQuery();
      return rs.getString("suggestionsChannel");

    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return "";
  }


  public void updateTime(int pollID, long time) {
    try {
      String createUser = "UPDATE polls SET time=? WHERE pollID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setLong(1, time);
      st.setInt(2, pollID);
      st.executeUpdate(); // Execute the statement
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public int addPoll(String guildID, String title, String channelID, long time, String messageID) {
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
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return -1;
  }

  public void updateTime(String pollID, long time) {
    try {
      String createUser = "UPDATE polls SET time=? WHERE pollID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setLong(1, time);
      st.setString(2, pollID);
      st.executeUpdate(); // Execute the statement
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void removePoll(String pollID) {
    try {
      String query = "DELETE FROM polls WHERE pollID=?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setString(1, pollID); // Insert using function to prevent SQL injection
      st.executeUpdate(); // Execute query
    } catch (Exception exc) {
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

  public void setOptions(int pollID,List<String> options) {
    try {
      for (String option : options) {
        String createUser = "INSERT INTO options (pollID,optionString) VALUES (?,?)";
        PreparedStatement st = con.prepareStatement(createUser);
        st.setInt(1, pollID);
        st.setString(2, option);
        st.executeUpdate();
      }
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public List<String> getOptions(int pollID) {
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
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return new ArrayList<>();
  }

  public void deleteOptions(int pollID) {
    try {
      String query = "DELETE FROM options WHERE pollID = ?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setInt(1, pollID); // Insert using function to prevent SQL injection
      st.executeUpdate(); // Execute query
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void deletePoll(int pollID) {
    try {
      String query = "DELETE FROM polls WHERE pollID = ?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setInt(1, pollID); // Insert using function to prevent SQL injection
      st.executeUpdate(); // Execute query
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void addPerm(String roleID, String permission) {
    try {
      String query = "INSERT INTO permissions (roleID,permission) VALUES (?,?)";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, roleID);
      st.setString(2, permission);
      st.executeUpdate();
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public void removePerm(String roleID, String permission) {
    try {
      String query = "DELETE FROM permissions WHERE roleID = ?  AND permission = ?";
      PreparedStatement st = con.prepareStatement(query); // Prepare the statement
      st.setString(1, roleID); // Insert using function to prevent SQL injection
      st.setString(2, permission);
      st.executeUpdate(); // Execute query
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public List<String> getPerms(String roleID) {
    try {
      String createUser = "SELECT permission FROM permissions WHERE roleID=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, roleID);
      ResultSet rs = st.executeQuery();
      List<String> perms = new ArrayList<>();
      while (rs.next()) {
        perms.add(rs.getString("permission"));
      }
      return perms;
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return new ArrayList<>();
  }

  public boolean checkPerm(String roleID, String permission) {
    try {
      String createUser = "SELECT permission FROM permissions WHERE roleID=? AND permission=?";
      PreparedStatement st = con.prepareStatement(createUser);
      st.setString(1, roleID);
      st.setString(2,permission);
      ResultSet rs = st.executeQuery();
      rs.last();
      return rs.getRow() > 0;
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    return false;
  }

  public void close() {
    try {
      con.close();
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }
}
