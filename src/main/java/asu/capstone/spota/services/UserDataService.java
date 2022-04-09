package asu.capstone.spota.services;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import asu.capstone.spota.model.News;
import asu.capstone.spota.model.NewsResult;
import asu.capstone.spota.model.Game;
import asu.capstone.spota.model.UserAccount;
import com.google.gson.Gson;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {
    @Value("${spring.datasource.url}")
    private String DB_URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PASS;

    @Autowired
    private NBAService nbaService;

    private static final Gson gson = new Gson();

    public UserDataService() {
        System.out.println("Service layer is created");

    }

    //this method adds a new user account to the DB and should be called after user account creation
    public boolean addNewUserAccount(UserAccount userAccount) {
        if(userAccount == null) {
            System.out.println("error: user account is null");
        }
       String sqlCommand = String.format("INSERT INTO Users (email, username, firstName, lastName, birthday)" +
                                         "values ('%s','%s', '%s', '%s', '%s');",
                                            userAccount.getEmail(),
                                            userAccount.getUsername(),
                                            userAccount.getFirstName(),
                                            userAccount.getLastName(),
                                            userAccount.getBirthday());

        if(updateDB(sqlCommand)) {
            System.out.println("successfully added new userAccount to DB");
            return true;
        } else {
            System.out.println("couldn't add user to DB");
            return false;
        }
    }

    //used to check if a user account exists in the DB
    public boolean userExists(String email) {
        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {
            String sqlQuery = String.format("SELECT * FROM Users WHERE email= '%s';", email);
            System.out.println(sqlQuery);

            //getting result set from DB
            ResultSet resultSet = stmt.executeQuery(sqlQuery);

            if (resultSet.next()) {
                //user exists in DB
                System.out.println("user exists");
                return true;
            } else {
                System.out.println("user doesn't exist");
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean usernameExists(String username) {
        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {
            String sqlQuery = String.format("SELECT * FROM Users WHERE username= '%s';", username);
            System.out.println(sqlQuery);

            //getting result set from DB
            ResultSet resultSet = stmt.executeQuery(sqlQuery);

            if (resultSet.next()) {
                //user exists in DB
                System.out.println("user exists");
                return true;
            } else {
                System.out.println("user doesn't exist");
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //adds a team to a users team subscription list
    public boolean addTeamsSubscription(String email, List<String> teamNames) {
        for(String teamName : teamNames) {
            String sqlCommand = String.format("INSERT INTO hasTeamSubscription (email, teamName) values ('%s', '%s');", email, teamName);
            if (!updateDB(sqlCommand)) {
                return false;
            }
        }
        return true;
    }

    //removes a team from a users team subscription list
    public boolean removeTeamsSubscription(String email, List<String> teamNames) {
        for(String teamName: teamNames) {
            String sqlCommand = String.format("DELETE FROM hasTeamSubscription WHERE email='%s' AND teamName='%s';", email, teamName);
            if (!updateDB(sqlCommand)) {
                return false;
            }
        }
        return true;
    }

    public boolean addFriend(String user1, String user2) {
        String sqlCommand = String.format("INSERT INTO hasFriend(user1, user2) values ('%s', '%s');", user1, user2);
        if(!updateDB(sqlCommand)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean removeFriend(String user1, String user2) {
        String sqlCommand = String.format("DELETE FROM hasFriend WHERE user1='%s' AND user2='%s';", user1, user2);
        if(!updateDB(sqlCommand)) {
            return false;
        } else {
            return true;
        }
    }

    //get a list of users who match a given prefix
    public String getUsersByPrefix(String prefix, String field) {
        if(prefix.length() > 0) {

            String sqlQuery = null;
            String prefixInsert = prefix + '%';

            if(field.equals("email")) {
                sqlQuery = String.format("SELECT * FROM users WHERE email LIKE '%s';", prefixInsert);
            } else if(field.equals("username")) {
                sqlQuery = String.format("SELECT * FROM users WHERE username LIKE '%s';", prefixInsert);
            } else {
                return "invalid field";
            }

            try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = dbc.createStatement();) {
                List<UserAccount> users = new ArrayList<>();
                ResultSet resultSet = stmt.executeQuery(sqlQuery);

                while (resultSet.next()) {
                    UserAccount user = new UserAccount();
                    List<String> friendsList = getUserFriends(resultSet.getString("email"));
                    user.setEmail(resultSet.getString("email"));
                    user.setFirstName(resultSet.getString("firstname"));
                    user.setLastName(resultSet.getString("lastname"));
                    user.setUsername(resultSet.getString("username"));
                    user.setBirthday(resultSet.getString("birthday"));
                    user.setProfile_color(resultSet.getString("profile_color"));
                    users.add(user);
                }
                return gson.toJson(users);

            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return "invalid prefix";
    }

    //getting a list of users who match the name passed in
    public String getUsersByName(String[] names) {
        if(names.length < 1) {
            return null;
        } else {
            try(Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = dbc.createStatement();) {
                String sqlQuery = null;
                List<UserAccount> users = new ArrayList<>();

                if(names.length == 1) {
                    sqlQuery = String.format("SELECT * FROM users WHERE firstname='%s' OR lastname='%s';", names[0]);
                } else if(names.length == 2) {
                    sqlQuery = String.format("SELECT * FROM users WHERE firstname='%s' AND lastname='%s';", names[0], names[1]);
                }

                if(sqlQuery != null) {
                    ResultSet resultSet = stmt.executeQuery(sqlQuery);

                    while(resultSet.next()) {
                        UserAccount user = new UserAccount();
                        List<String> friendsList = getUserFriends(resultSet.getString("email"));
                        user.setEmail(resultSet.getString("email"));
                        user.setFirstName(resultSet.getString("firstname"));
                        user.setLastName(resultSet.getString("lastname"));
                        user.setUsername(resultSet.getString("username"));
                        user.setBirthday(resultSet.getString("birthday"));
                        user.setProfile_color(resultSet.getString("profile_color"));
                        users.add(user);
                    }
                    return gson.toJson(users);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //getting a user from the DB
    public String getUserByEmail(String userEmail) {
        if(!userExists(userEmail)) {
            return null;
        } else {
            try(Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = dbc.createStatement();) {
                String sqlQuery = String.format("SELECT * FROM users WHERE email='%s';", userEmail);

                ResultSet resultSet = stmt.executeQuery(sqlQuery);

                if(resultSet.next()) {
                    UserAccount user = new UserAccount();
                    List<String> friendsList = getUserFriends(userEmail);
                    user.setEmail(userEmail);
                    user.setFirstName(resultSet.getString("firstname"));
                    user.setLastName(resultSet.getString("lastname"));
                    user.setUsername(resultSet.getString("username"));
                    user.setBirthday(resultSet.getString("birthday"));
                    user.setProfile_color(resultSet.getString("profile_color"));
                    return gson.toJson(user);
                }

            } catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public String getUserByUsername(String username) {
        if(!usernameExists(username)) {
            return null;
        } else {
            try(Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = dbc.createStatement();) {
                String sqlQuery = String.format("SELECT * FROM users WHERE username='%s';", username);

                ResultSet resultSet = stmt.executeQuery(sqlQuery);

                if(resultSet.next()) {
                    UserAccount user = new UserAccount();
                    List<String> friendsList = getUserFriends(resultSet.getString("email"));
                    user.setUsername(username);
                    user.setEmail(resultSet.getString("email"));
                    user.setFirstName(resultSet.getString("firstname"));
                    user.setLastName(resultSet.getString("lastname"));
                    user.setBirthday(resultSet.getString("birthday"));
                    user.setProfile_color(resultSet.getString("profile_color"));
                    return gson.toJson(user);
                }

            } catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public List<String> getUserFriends(String email) {
        if(!userExists(email)) {
            return null;
        } else {
            try(Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = dbc.createStatement();) {
                String sqlQuery = String.format("SELECT * FROM hasFriend WHERE user1='%s';", email);

                ResultSet resultSet = stmt.executeQuery(sqlQuery);

                List<String> friendsList = new ArrayList<>();

                while(resultSet.next()) {
                    String friend = resultSet.getString("user2");
                    friendsList.add(friend);
                }
                return friendsList;

            } catch(SQLException e) {
                System.out.println(e);
                return null;
            }
        }
    }

    //getting specific news for a user according to their subscribed teams
    public String getNews(String userEmail) throws IOException, InterruptedException {
        if(!userExists(userEmail)) {
            return "user doesn't exist";
        }

        ArrayList<String> teamSubscriptions = new ArrayList<>();

        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {

            String sqlQuery = String.format("SELECT * FROM hasTeamSubscription where email='%s';", userEmail);

            //getting result set from DB
            ResultSet resultSet = stmt.executeQuery(sqlQuery);

            while(resultSet.next()) {
                //adding teams to the subscriptions list
                teamSubscriptions.add(resultSet.getString("teamname"));
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        if(teamSubscriptions.size() == 0) {
            return null;
        }

        List<NewsResult> newsResults = nbaService.getNews(teamSubscriptions);
        return gson.toJson(newsResults);
    }

    //getting general news for a user with no team subscriptions or an unauthenticated user
    public String getGeneralNews() throws IOException, InterruptedException {
        News[] newsResults = nbaService.getGeneralNews();
        return gson.toJson(newsResults);
    }

    //getting specific scores for a user according to their subscribed teams
    public String getScores(String userEmail) {

        if(!userExists(userEmail)) {
            return "user doesn't exist";
        }
        //team subscription list for the user
        ArrayList<String> teamSubscriptions = new ArrayList<>();


        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {

            String sqlQuery = String.format("SELECT * FROM hasTeamSubscription where email='%s';", userEmail);

            //getting result set from DB
            ResultSet resultSet = stmt.executeQuery(sqlQuery);

            while(resultSet.next()) {
                //adding teams to the subscriptions list
                teamSubscriptions.add(resultSet.getString("teamname"));
            }

        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }

        if(teamSubscriptions.size() == 0) {
            return null;
        }

        List<Game> scores = nbaService.getScores(teamSubscriptions);

        if(scores == null) {
            return null;
        }

        return gson.toJson(scores);
    }

    public String getGeneralScores() throws IOException, InterruptedException {
        List<Game> scores = nbaService.getGeneralScores();

        if(scores == null) {
            return null;
        }

        return gson.toJson(scores);
    }

    public boolean updateDB(String sqlCommand) {
        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {

            System.out.println(sqlCommand);

            stmt.executeUpdate(sqlCommand); //updating the DB with INSERT, UPDATE, DELETE

            stmt.close();
            dbc.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet queryDB(String sqlCommand) {
        try (Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = dbc.createStatement();) {

            //getting result set from DB
            ResultSet resultSet = stmt.executeQuery(sqlCommand);

            stmt.close();
            return resultSet;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
