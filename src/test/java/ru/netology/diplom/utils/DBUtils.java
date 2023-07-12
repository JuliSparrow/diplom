package ru.netology.diplom.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBUtils {
    private static Connection connection;

    public static void initConnection() {
        Properties properties = new Properties();
        String fileName = "application.properties";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = System.getProperty("spring.datasource.url") != null
                ? System.getProperty("spring.datasource.url")
                : properties.getProperty("spring.datasource.url");
        String user = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password");

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getOrderEntityCount() {
        return selectCountFromTable("order_entity");
    }

    public static int getPaymentEntityCount() {
        return selectCountFromTable("payment_entity");
    }

    public static int getCreditRequestEntityCount() {
        return selectCountFromTable("credit_request_entity");
    }

    private static int selectCountFromTable(String tableName) {
        String query = "SELECT count(*) as count FROM " + tableName;
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt("count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPaymentEntityStatus() {
        String query = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getString("status");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCreditRequestEntityStatus() {
        String query = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getString("status");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
