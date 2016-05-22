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

    public UserInfo getUserInfo(String userName, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE login = '" +
                userName + "' AND password = '" + password + "';";
        ResultSet resultSet = executeQuery(query);
        if (resultSet.next()) {
            UserInfo userInfo = new UserInfo();
            userInfo.name = "JENYA225";
            System.out.println("ZPDFSDFSDF");
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
