package org.example;

import org.example.database.DBGroup;
import org.example.packetProcessing.Processor;
import org.example.warehouse.Group;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DBGroupTest {
    private DBGroup dbGroup;

    @Before
    public void setUp() {
        dbGroup = new DBGroup(Processor.dbFile);
        dbGroup.dropGroupsTable();
        dbGroup.initGroupsTable();
    }

    @After
    public void tearDown() {
        dbGroup.close();
    }

    @Test
    public void testCreatingGroups() {
        int id = dbGroup.addGroup(new Group("хлібобулочні вироби", "хлібобулочні вироби"));
        assertTrue(id > 0);
        dbGroup.deleteGroup(id);
    }

    @Test
    public void testGettingGroup() {
        int id = dbGroup.addGroup(new Group("молочні продукти", "молочні продукти"));
        Group readGroup = dbGroup.readGroup(id);
        assertNotNull(readGroup);
        assertEquals("молочні продукти", readGroup.getName());
        dbGroup.deleteGroup(id);
    }

    @Test
    public void testUpdatingGroup() {
        int id = dbGroup.addGroup(new Group("крупи", "крупи"));
        Group updatedGroup = new Group(id, "крупи", "органічні крупи");
        int result = dbGroup.updateGroup(updatedGroup);
        assertEquals(id, result);
        Group readGroup = dbGroup.readGroup(id);
        assertEquals("органічні крупи", readGroup.getDescription());
        dbGroup.deleteGroup(id);
    }

    @Test
    public void testDeletingGroup() {
        int id = dbGroup.addGroup(new Group("фрукти", "фрукти"));
        int result = dbGroup.deleteGroup(id);
        assertEquals(id, result);
        Group readGroup = dbGroup.readGroup(id);
        assertNull(readGroup);
    }

    @Test
    public void testAddGroupWithDuplicateName() {
        int id1 = dbGroup.addGroup(new Group("овочі", "овочі"));
        int id2 = dbGroup.addGroup(new Group("овочі", "овочі органічні"));
        assertEquals(-1, id2);
        dbGroup.deleteGroup(id1);
    }

    @Test
    public void testUpdateGroupWithNonUniqueName() {
        int id1 = dbGroup.addGroup(new Group("напої", "різні види напоїв"));
        int id2 = dbGroup.addGroup(new Group("заморожені продукти", "різні заморожені продукти"));
        int result = dbGroup.updateGroup(new Group(id2, "напої", "різні заморожені продукти"));
        assertEquals(-2, result);
        dbGroup.deleteGroup(id1);
        dbGroup.deleteGroup(id2);
    }

    @Test
    public void testUpdateNonExistentGroup() {
        Group group = new Group(999, "сири", "різні види сирів");
        int result = dbGroup.updateGroup(group);
        assertEquals(-1, result);
    }

    @Test
    public void testDeleteNonExistentGroup() {
        int result = dbGroup.deleteGroup(999);
        assertEquals(-1, result);
    }
}