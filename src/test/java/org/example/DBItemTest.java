package org.example;

import org.example.database.DAOFactory;
import org.example.database.interfaces.IGroupDAO;
import org.example.database.interfaces.IItemDAO;
import org.example.warehouse.Group;
import org.example.warehouse.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DBItemTest {
    private IGroupDAO dbGroup;
    private IItemDAO dbItem;
    private DAOFactory factory;

    @Before
    public void setUp() {
        factory = new DAOFactory("data.db");
        dbGroup = factory.createGroupDAO();
        dbGroup.dropGroupsTable();
        dbGroup.initGroupsTable();

        dbItem = factory.createItemDAO();
        dbItem.dropItemsTable();
        dbItem.initItemsTable();
    }

    @After
    public void tearDown() {
        factory.close();
    }

    @Test
    public void testCreatingItem() {
        int groupId = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));

        Item item = new Item("молоко", "свіже молоко", "Постачальник 1", 100, 20.5, groupId);
        int id = dbItem.addItem(item);
        assertTrue(id > 0);
        dbItem.deleteItem(id);
    }

    @Test
    public void testGettingItem() {
        int groupId = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));

        Item item = new Item("молоко", "свіже молоко", "Постачальник 1", 100, 20.5, groupId);
        int id = dbItem.addItem(item);
        Item readItem = dbItem.readItem(id);
        assertNotNull(readItem);
        assertEquals("молоко", readItem.getName());
        dbItem.deleteItem(id);
    }

    @Test
    public void testUpdatingItem() {
        int groupId = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));

        Item item = new Item("молоко", "свіже молоко", "Постачальник 1", 100, 20.5, groupId);
        int id = dbItem.addItem(item);
        Item updatedItem = new Item(id, "молоко", "органічне молоко", "Постачальник 1", 100, 25.0, groupId);
        int result = dbItem.updateItem(updatedItem);
        assertEquals(id, result);
        Item readItem = dbItem.readItem(id);
        assertEquals("органічне молоко", readItem.getDescription());
        dbItem.deleteItem(id);
    }

    @Test
    public void testDeletingItem() {
        int groupId = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));

        Item item = new Item("молоко", "свіже молоко", "Постачальник 1", 100, 20.5, groupId);
        int id = dbItem.addItem(item);
        int result = dbItem.deleteItem(id);
        assertEquals(id, result);
        Item readItem = dbItem.readItem(id);
        assertNull(readItem);
    }

    @Test
    public void testAddItemWithDuplicateName() {
        int groupId = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));

        Item item1 = new Item("молоко", "свіже молоко", "Постачальник 1", 100, 20.5, groupId);
        int id1 = dbItem.addItem(item1);
        Item item2 = new Item("молоко", "органічне молоко", "Постачальник 1", 100, 25.0, groupId);
        int id2 = dbItem.addItem(item2);
        assertEquals(-1, id2);
        dbItem.deleteItem(id1);
    }

    @Test
    public void testUpdateItemWithNonUniqueName() {
        int groupId = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));

        Item item1 = new Item("молоко", "свіже молоко", "Постачальник 1", 100, 20.5, groupId);
        int id1 = dbItem.addItem(item1);
        Item item2 = new Item("кефір", "свіжий кефір", "Постачальник 2", 50, 15.0, groupId);
        int id2 = dbItem.addItem(item2);
        Item updatedItem = new Item(id2, "молоко", "свіжий кефір", "Постачальник 2", 50, 15.0, groupId);
        int result = dbItem.updateItem(updatedItem);
        assertEquals(-2, result);
        dbItem.deleteItem(id1);
        dbItem.deleteItem(id2);
    }

    @Test
    public void testUpdateNonExistentItem() {
        int groupId = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));

        Item item = new Item(999, "сир", "різні види сирів", "Постачальник 3", 200, 30.0, groupId);
        int result = dbItem.updateItem(item);
        assertEquals(-1, result);
    }

    @Test
    public void testDeleteNonExistentItem() {
        int result = dbItem.deleteItem(999);
        assertEquals(-1, result);
    }
}
