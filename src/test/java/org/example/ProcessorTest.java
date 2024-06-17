package org.example;

import com.google.common.primitives.UnsignedLong;
import org.example.database.DBGroup;
import org.example.packet.Message;
import org.example.packet.Packet;
import org.example.packetProcessing.Processor;
import org.example.warehouse.Group;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProcessorTest {

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
    public void testAddingGroup() {
        // Ensure the group does not already exist
        Group existingGroup = dbGroup.getGroupByName("фрукти");
        if (existingGroup != null) {
            dbGroup.deleteGroup(existingGroup.getId());
        }

        Group group = new Group("фрукти", "фрукти");
        Message message = new Message(Message.cTypes.ADD_GROUP.ordinal(), 1, group.toJSON().toString());
        Packet packet = new Packet((byte) 1, UnsignedLong.ONE, message);
        byte[] response = Processor.processPacketAndGetAnswer(packet);
        try {
            Packet responsePacket = new Packet(response);
            String responseMessage = responsePacket.getBMsq().getMessageString();
            assertEquals("Successfully added group", responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during processing packet");
        }
    }
}
