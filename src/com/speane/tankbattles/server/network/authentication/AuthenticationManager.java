package com.speane.tankbattles.server.network.authentication;

import com.speane.tankbattles.server.application.UserInfo;
import com.speane.tankbattles.server.database.DatabaseConnection;

import java.sql.SQLException;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class AuthenticationManager {
    private DatabaseConnection databaseConnection;

    public AuthenticationManager() throws SQLException {
        databaseConnection = new DatabaseConnection("localhost", "tank_battles_scheme", "speane", "123456QWERTY");
    }

    public UserInfo getUserInfo(String userName, String password) throws SQLException {
        return databaseConnection.getUserInfo(userName, password);
    }

    public UserInfo register(String userName, String password, String email) throws SQLException {
        databaseConnection.execute("INSERT INTO users (login, password, email, bestScore, battlesPlayed) VALUES ('"
                + userName + "', '" + password + "', '" + email + "', 0, 0);");
        return getUserInfo(userName, password);
    }

    public UserInfo updateUserInfo(UserInfo userInfo) throws SQLException {
        return databaseConnection.updateUserInfo(userInfo);
    }
}
