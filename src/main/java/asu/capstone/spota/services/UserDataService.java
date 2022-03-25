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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {
    @Value("${spring.datasource.url}")
    private String DB_URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PASS;

    //private final String DB_URL = "jdbc:postgres://fxlzstcmjwqycu:ad14bd3574fe265e40781011d35ba6619f1ede13bb3a529d7e30933bd85eda95@ec2-23-21-4-7.compute-1.amazonaws.com:5432/d6hr3nji5eoibj";
    //private final String USER = "fxlzstcmjwqycu";
    //private final String PASS = "ad14bd3574fe265e40781011d35ba6619f1ede13bb3a529d7e30933bd85eda95";

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
        System.out.println(DB_URL + "\n" + USER + "\n" + PASS);

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

    public String getGeneralScores() {
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
