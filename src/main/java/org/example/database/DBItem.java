package org.example.database;

import org.example.database.interfaces.IItemDAO;
import org.example.warehouse.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBItem implements IItemDAO {

    final Connection connection;
    public static final String tableName = "items";

    public DBItem(final Connection connection) {
        this.connection = connection;
        initItemsTable();
    }

    @Override
    public void initItemsTable() {
        try {
            final Statement statement = connection.createStatement();
            String query = "create table if not exists " + tableName +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "'name' text not null, " +
                    "'description' text not null, " +
                    "'supplier' text not null, " +
                    "'stock_qty' decimal not null, " +
                    "'price' real not null, " +
                    "'group_id' integer not null, foreign key(group_id) references groups(id) ON UPDATE CASCADE ON DELETE CASCADE);";
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create items table", e);
        }
    }

    @Override
    public void dropItemsTable() {
        try {
            final Statement statement = connection.createStatement();
            String query = "drop table if exists " + tableName;
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't drop items table", e);
        }
    }

    @Override
    public Item readItem(final int id) {
        try {
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select * from " + tableName + " where id = ?");
            selectStatement.setInt(1, id);
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return new Item(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("supplier"),
                        resultSet.getInt("stock_qty"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("group_id"));
            } else return null;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get item", e);
        }
    }

    @Override
    public int addItem(final Item item) {
        if (isNameUnique(item.getName())) {
            String query = "insert into " + tableName +
                    " ('name', 'description', 'supplier', 'stock_qty', 'price', 'group_id') values (?, ?, ?, ?, ?, ?);";
            try {
                final PreparedStatement addItemStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                addItemStatement.setString(1, item.getName());
                addItemStatement.setString(2, item.getDescription());
                addItemStatement.setString(3, item.getSupplier());
                addItemStatement.setInt(4, item.getStock_qty());
                addItemStatement.setDouble(5, item.getPrice());
                addItemStatement.setInt(6, item.getGroup_id());
                addItemStatement.execute();
                final ResultSet result = addItemStatement.getGeneratedKeys();
                return result.getInt("last_insert_rowid()");
            } catch (SQLException e) {
                throw new RuntimeException("Can't add item", e);
            }
        }
        return -1;
    }

    @Override
    public int updateItem(final Item item) {
        try {
            String query = "update " + tableName + " set name = ?, description = ?, supplier = ?, stock_qty = ?, price = ?, group_id = ?  where id = ?";
            final PreparedStatement updateStatement = connection.prepareStatement(query);
            updateStatement.setString(1, item.getName());
            updateStatement.setString(2, item.getDescription());
            updateStatement.setString(3, item.getSupplier());
            updateStatement.setInt(4, item.getStock_qty());
            updateStatement.setDouble(5, item.getPrice());
            updateStatement.setInt(6, item.getGroup_id());
            updateStatement.setInt(7, item.getId());
            // check unique name of item
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select name from " + tableName + " where id = ?");
            selectStatement.setInt(1, item.getId());
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            String name = "";
            if (resultSet.next()) {
                name = resultSet.getString("name");
            }
            if (name.equals("")) {
                return -1; // there is no item with such id
            }
            if (isNameUnique(item.getName()) || item.getName().equals(name)) {
                updateStatement.executeUpdate();
                return item.getId();
            } else {
                return -2; // not unique name of item
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't update item", e);
        }
    }

    @Override
    public boolean isNameUnique(final String itemName) {
        try {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(
                    String.format("select count(*) as num_of_items from " + tableName + " where name = '%s'", itemName)
            );
            resultSet.next();
            return resultSet.getInt("num_of_items") == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't add item", e);
        }
    }

    @Override
    public int deleteItem(final int id) {
        try {
            String query = "delete from " + tableName + " where id = ?";
            final PreparedStatement deleteStatement = connection.prepareStatement(query);
            deleteStatement.setInt(1, id);
            int isDeleted = deleteStatement.executeUpdate();
            if (isDeleted == 1) {
                return id;
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete item", e);
        }
    }

    @Override
    public void deleteAllItemsInGroup(final int groupId) {
        try {
            final Statement statement = connection.createStatement();
            String query = String.format("delete from " + tableName + " where group_id = '%s'", groupId);
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete items", e);
        }
    }

    @Override
    public List<Item> getAllItemsFromGroup(final int groupId) {
        List<Item> itemsFromGroup = new ArrayList<>();
        try {
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select * from " + tableName + " where group_id = ?");
            selectStatement.setInt(1, groupId);
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                Item item = new Item(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("supplier"),
                        resultSet.getInt("stock_qty"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("group_id"));
                itemsFromGroup.add(item);
            }
            return itemsFromGroup;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get items", e);
        }
    }

    @Override
    public List<Item> getAllItemsWithPrice(final double fromPrice, final double toPrice) {
        List<Item> itemsWithSpecificPrice = new ArrayList<>();
        try {
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select * from " + tableName + " where price between ? and ?");
            selectStatement.setDouble(1, fromPrice);
            selectStatement.setDouble(2, toPrice);
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                Item item = new Item(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("supplier"),
                        resultSet.getInt("stock_qty"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("group_id"));
                itemsWithSpecificPrice.add(item);
            }
            return itemsWithSpecificPrice;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get items", e);
        }
    }

    @Override
    public List<Item> getAllItemsWithSupplier(String supplier) {
        List<Item> itemsWithSpecificSupplier = new ArrayList<>();
        try {
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select * from " + tableName + " where supplier = ?");
            selectStatement.setString(1, supplier);
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                Item item = new Item(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("supplier"),
                        resultSet.getInt("stock_qty"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("group_id"));
                itemsWithSpecificSupplier.add(item);
            }
            return itemsWithSpecificSupplier;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get items", e);
        }
    }

    @Override
    public List<Item> getAllItemsWithLowStock(final int lowStock) {
        List<Item> itemsWithLowStock = new ArrayList<>();
        try {
            final PreparedStatement selectStatement = connection.prepareStatement(
                    "select * from " + tableName + " where stock_qty < ?");
            selectStatement.setInt(1, lowStock);
            selectStatement.execute();
            final ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                Item item = new Item(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("supplier"),
                        resultSet.getInt("stock_qty"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("group_id"));
                itemsWithLowStock.add(item);
            }
            return itemsWithLowStock;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get items", e);
        }
    }
}
