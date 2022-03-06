package asu.capstone.spota.services;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import asu.capstone.spota.model.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {
    private final String DB_URL = "jdbc:postgresql://localhost/spotatest";
    private final String USER = "postgres";
    private final String PASS = "123";

    public UserDataService() {
        System.out.println("Service layer is created");

        /*try(Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = dbc.createStatement();) {

            String sql = "CREATE TABLE Users(" +
                            "email text PRIMARY KEY, " +
                            "firstName text, " +
                            "lastName text, " +
                            "username text, " +
                            "birthday timestamp); ";

            stmt.executeUpdate(sql);
            System.out.println("users table created successfully");
        } catch(SQLException e) {
            e.printStackTrace();
        }*/
    }

    //this method adds a new user account to the DB and should be called after user account creation
    public boolean addNewUserAccount(UserAccount userAccount) {
        if(userAccount == null) {
            System.out.println("error: user account is null");
        }
        try(Connection dbc = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = dbc.createStatement();) {

           String sqlCommand = String.format("INSERT INTO Users (email, firstName, lastName, username)" +
                                             "values ('%s','%s','%s','%s');",
                                                userAccount.getEmail(),
                                                userAccount.getFirstName(),
                                                userAccount.getLastName(),
                                                userAccount.getUsername());

            System.out.println(sqlCommand);

            stmt.executeUpdate(sqlCommand);
            System.out.println("successfully added new userAccount to DB");
            return true;

        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
