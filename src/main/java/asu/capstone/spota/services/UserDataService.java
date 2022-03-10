package asu.capstone.spota.services;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import asu.capstone.spota.model.NewsResult;
import asu.capstone.spota.model.Game;
import asu.capstone.spota.model.UserAccount;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {
    private final String DB_URL = "jdbc:postgresql://localhost/spotadev";
    private final String USER = "postgres";
    private final String PASS = "123";

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

    public boolean addTeamSubscription(String email, String teamName) {
        String sqlCommand = String.format("INSERT INTO hasTeamSubscription (email, teamName) values ('%s', '%s');", email, teamName);
        if(updateDB(sqlCommand)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeTeamSubscription(String email, String teamName) {
        String sqlCommand = String.format("DELETE FROM hasTeamSubscription WHERE email='%s' AND teamName='%s';", email, teamName);
        if(updateDB(sqlCommand)) {
            return true;
        } else {
            return false;
        }
    }

    public String getNews(String userEmail) throws IOException, InterruptedException {
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
        List<NewsResult> newsResults = nbaService.getNews(teamSubscriptions);
        return gson.toJson(newsResults);
    }

    public String getScores(String userEmail) {
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
        }
        List<Game> scores = nbaService.getScores(teamSubscriptions);
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
