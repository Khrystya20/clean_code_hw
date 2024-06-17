package org.example.database;

import org.example.database.interfaces.IGroupDAO;
import org.example.warehouse.Group;

import java.sql.*;

public class DBGroup implements IGroupDAO {

    private final Connection connection;
    public static final String tableName = "groups";

    public DBGroup(final Connection connection) {
        this.connection = connection;
        initGroupsTable();
    }

    @Override
    public void initGroupsTable() {
        try {
            final Statement statement = connection.createStatement();
            String query = "create table if not exists " + tableName +
                    " ('id' INTEGER PRIMARY KEY, 'name' text not null," +
                    " 'description' text not null, unique(name));";
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create groups table", e);
        }
    }

    @Override
    public void dropGroupsTable() {
        try {
            final Statement statement = connection.createStatement();
            String query = "drop table if exists " + tableName;
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't drop groups table", e);
        }
    }

    @Override
    public Group readGroup(final int id) {
        try {
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select * from " + tableName + " where id = ?");
            selectStatement.setInt(1, id);
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return new Group(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"));
            } else return null;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get group", e);
        }
    }

    @Override
    public int addGroup(final Group group) {
        if (isNameUnique(group.getName())) {
            String query = "insert into " + tableName
                    + " ('name', 'description') values (?, ?);";
            try {
                final PreparedStatement addGroupStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                addGroupStatement.setString(1, group.getName());
                addGroupStatement.setString(2, group.getDescription());
                addGroupStatement.execute();
                final ResultSet result = addGroupStatement.getGeneratedKeys();
                return result.getInt("last_insert_rowid()");
            } catch (SQLException e) {
                throw new RuntimeException("Can't add group", e);
            }
        }
        return -1;
    }

    @Override
    public int updateGroup(final Group group) {
        try {
            String query = "update " + tableName + " set name = ?, description = ?  where id = ?";
            final PreparedStatement updateStatement = connection.prepareStatement(query);
            updateStatement.setString(1, group.getName());
            updateStatement.setString(2, group.getDescription());
            updateStatement.setInt(3, group.getId());
            // check unique name of group
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select name from " + tableName + " where id = ?");
            selectStatement.setInt(1, group.getId());
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            String name = "";
            if (resultSet.next()) {
                name = resultSet.getString("name");
            }
            if (name.equals("")) {
                return -1; // there is no group with such id
            }
            if (isNameUnique(group.getName()) || group.getName().equals(name)) {
                updateStatement.executeUpdate();
                return group.getId();
            } else {
                return -2; // not unique name of group
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't update group", e);
        }
    }

    @Override
    public boolean isNameUnique(final String groupName) {
        try {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(
                    String.format("select count(*) as num_of_groups from " + tableName + " where name = '%s'", groupName)
            );
            resultSet.next();
            return resultSet.getInt("num_of_groups") == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find group", e);
        }
    }

    @Override
    public int deleteGroup(final int id) {
        try {
            String query = "delete from " + tableName + " where id = ?";
            final PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int isDeleted = preparedStatement.executeUpdate();
            if (isDeleted == 1) {
                return id;
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete group", e);
        }
    }

    @Override
    public Group getGroupByName(String name) {
        try {
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select * from " + tableName + " where name = ?");
            selectStatement.setString(1, name);
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return new Group(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"));
            } else return null;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get group", e);
        }
    }
}
