package org.example.database;

import org.example.database.interfaces.IGroupDAO;
import org.example.database.interfaces.IItemDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOFactory {

    private final String dbFile;
    private Connection connection;

    public DAOFactory(String dbFile) {
        this.dbFile = dbFile;
    }

    private Connection createConnection() {
        if (connection == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            } catch (ClassNotFoundException e) {
                System.out.println("Can't load SQLite JDBC class");
                throw new RuntimeException("Can't find class", e);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public IGroupDAO createGroupDAO() {
        return new DBGroup(createConnection());
    }

    public IItemDAO createItemDAO() {
        return new DBItem(createConnection());
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed");
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
