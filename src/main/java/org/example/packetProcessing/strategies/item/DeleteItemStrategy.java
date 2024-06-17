package org.example.packetProcessing.strategies.item;

import org.example.database.interfaces.IItemDAO;
import org.example.packet.Packet;
import org.example.packetProcessing.strategies.CommandStrategy;

public class DeleteItemStrategy implements CommandStrategy {
    private final IItemDAO itemDAO;

    public DeleteItemStrategy(IItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public String execute(Packet packet) {
        String message = packet.getBMsq().getMessageString();
        int itemId = itemDAO.deleteItem(Integer.parseInt(message));
        if (itemId < 0) {
            return "Incorrect id of item";
        } else {
            return "Successfully deleted item";
        }
    }
}
