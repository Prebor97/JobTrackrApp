package com.jobtrackr.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/jobTrackerdb";
    private static final String USER = "your-user-name";
    private static final String PASSWORD = "your-password";

    public static Connection getConnection(){
        Connection connection = null;
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.out.println("PostgresSQL JDBC Driver not found.");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.out.println("Connection failed. ");
            throw new RuntimeException(e);
        }
        return connection;

    }
}
