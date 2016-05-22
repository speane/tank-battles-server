package com.speane.tankbattles.server.database;

import com.speane.tankbattles.server.application.UserInfo;

import java.sql.*;

/**
 * Created by Evgeny Shilov on 22.05.2016.
 */
public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection(String host, String dataBase, String login, String password) throws SQLException {
        String url = "jdbc:mysql://" + host + "/" + dataBase + "?useSSL=true";
        connection = DriverManager.getConnection(url, login, password);
    }

    public UserInfo updateUserInfo(UserInfo userInfo) throws SQLException {
        String query = "UPDATE users SET bestScore = '" + userInfo.bestScore + "', battlesPlayed = '"
                + userInfo.battlesPlayed + "' WHERE login = '" + userInfo.name + "';";
        execute(query);
        return userInfo;
    }

    public UserInfo getUserInfo(String userName, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE login = '" +
                userName + "' AND password = '" + password + "';";
        ResultSet resultSet = executeQuery(query);
        if (resultSet.next()) {
            UserInfo userInfo = new UserInfo();
            userInfo.name = resultSet.getString("login");
            userInfo.battlesPlayed = resultSet.getInt("battlesPlayed");
            userInfo.bestScore = resultSet.getInt("bestScore");
            return userInfo;
        }
        else {
            return null;
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        System.out.println(query);
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public void execute(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query);
    }
}
