package org.example;

import com.google.common.primitives.UnsignedLong;
import org.example.database.DAOFactory;
import org.example.packet.Message;
import org.example.packet.Packet;
import org.example.packetProcessing.Processor;
import org.example.warehouse.Group;
import org.example.database.interfaces.IGroupDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProcessorTest {

    private IGroupDAO dbGroup;
    private DAOFactory factory;

    @Before
    public void setUp() {
        factory = new DAOFactory("data.db");
        dbGroup = factory.createGroupDAO();
        dbGroup.dropGroupsTable();
        dbGroup.initGroupsTable();
    }

    @After
    public void tearDown() {
        factory.close();
    }

    @Test
    public void testAddingGroup() {
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
