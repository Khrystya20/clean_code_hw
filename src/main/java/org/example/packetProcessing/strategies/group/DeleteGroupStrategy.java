package org.example.packetProcessing.strategies.group;

import org.example.database.interfaces.IGroupDAO;
import org.example.database.interfaces.IItemDAO;
import org.example.packet.Packet;
import org.example.packetProcessing.strategies.CommandStrategy;

public class DeleteGroupStrategy implements CommandStrategy {
    private final IGroupDAO groupDAO;
    private final IItemDAO itemDAO;

    public DeleteGroupStrategy(IGroupDAO groupDAO, IItemDAO itemDAO) {
        this.groupDAO = groupDAO;
        this.itemDAO = itemDAO;
    }

    @Override
    public String execute(Packet packet) {
        String message = packet.getBMsq().getMessageString();
        int groupId = groupDAO.deleteGroup(Integer.parseInt(message));
        if (groupId < 0) {
            return "Incorrect id of group";
        } else {
            itemDAO.deleteAllItemsInGroup(groupId);
            return "Successfully deleted group";
        }
    }
}
