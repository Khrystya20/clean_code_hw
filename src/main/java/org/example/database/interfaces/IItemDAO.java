package org.example.database.interfaces;

import org.example.warehouse.Item;

import java.util.List;

public interface IItemDAO {
    void initItemsTable();
    void dropItemsTable();
    Item readItem(int id);
    int addItem(Item item);
    int updateItem(Item item);
    boolean isNameUnique(String itemName);
    int deleteItem(int id);
    void deleteAllItemsInGroup(int groupId);
    List<Item> getAllItemsFromGroup(int groupId);
    List<Item> getAllItemsWithPrice(double fromPrice, double toPrice);
    List<Item> getAllItemsWithSupplier(String supplier);
    List<Item> getAllItemsWithLowStock(int lowStock);
}