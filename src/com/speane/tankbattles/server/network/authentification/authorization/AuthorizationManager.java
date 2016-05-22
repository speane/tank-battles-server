package com.speane.tankbattles.server.network.authentification.authorization;

import com.speane.tankbattles.server.application.UserInfo;
import com.speane.tankbattles.server.database.DatabaseConnection;

import java.sql.SQLException;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class AuthorizationManager {
    private DatabaseConnection databaseConnection;

    public AuthorizationManager() throws SQLException {
        databaseConnection = new DatabaseConnection("localhost", "tank_battles_scheme", "speane", "123456QWERTY");
    }

    public UserInfo getUserInfo(String userName, String password) throws SQLException {
        return databaseConnection.getUserInfo(userName, password);
    }
}
