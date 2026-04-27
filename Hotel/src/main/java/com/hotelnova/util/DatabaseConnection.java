package com.hotelnova.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static Connection connection = null;

    // Método para obtener la conexión única
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties props = new Properties();

                props.load(new FileInputStream("src/main/resources/config.properties"));

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String pass = props.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, pass);
            } catch (IOException e) {
                throw new SQLException("Error al leer archivo de configuración", e);
            }
        }
        return connection;
    }
}