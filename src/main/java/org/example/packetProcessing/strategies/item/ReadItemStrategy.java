package org.example.packetProcessing.strategies.item;

import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.warehouse.Item;
import org.example.database.interfaces.IItemDAO;
import org.example.packet.Packet;

public class ReadItemStrategy implements CommandStrategy {
    private final IItemDAO itemDAO;

    public ReadItemStrategy(IItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public String execute(Packet packet) {
        String message = packet.getBMsq().getMessageString();
        Item readItem = itemDAO.readItem(Integer.parseInt(message));
        if (readItem != null) {
            return readItem.toJSON().toString();
        } else {
            return "Incorrect id of item";
        }
    }
}