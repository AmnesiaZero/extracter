package org.example.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DataSource {
    static String url ;
    static String username;
    static String password;
    public Connection connection;
    public DataSource() throws Exception {
        url = "jdbc:mysql://5.188.136.155:2230/vkr-smart-db1-pile";
        username = "vkr-smart-db1-parser";
        password = "A2xBaAMNESIAjKTSN";
        connection =  DriverManager.getConnection(url,username,password);
    }
}
