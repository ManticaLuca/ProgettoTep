package me.progettotep.spesaonline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Luca Mantica
 */
public class DbManager {
    private static DbManager INSTANCE;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String dbms_url = "jdbc:mysql://localhost/";
    private static final String database = "progetto_spesa_online";
    private static final String user = "root";
    private static final String password = "";
    private Connection connection;
    private boolean connected;

    private DbManager() {
        String url = dbms_url + database + "?serverTimezone=Europe/Rome";
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException | ClassNotFoundException e) {
            connected = false;
            e.printStackTrace();
        }
        System.out.println("CONFIG - " + connected);
    }

    public static synchronized DbManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbManager();
        }
        return INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return connected;
    }

}