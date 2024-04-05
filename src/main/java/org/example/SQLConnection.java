package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/LAB4_TP2";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection connection;

    // Método para obtener la conexión
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Cargar el driver JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa"); // Mensaje de conexión exitosa
            } catch (ClassNotFoundException e) {
                System.out.println("Error: No se pudo cargar el driver JDBC");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Error: No se pudo establecer la conexión a la base de datos");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Método para cerrar la conexión
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión a la base de datos");
                e.printStackTrace();
            }
        }
    }
}


