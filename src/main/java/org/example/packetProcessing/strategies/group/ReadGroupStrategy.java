package org.example.packetProcessing.strategies.group;

import org.example.database.interfaces.IGroupDAO;
import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.warehouse.Group;
import org.example.packet.Packet;

public class ReadGroupStrategy implements CommandStrategy {
    private final IGroupDAO groupDAO;

    public ReadGroupStrategy(IGroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }

    @Override
    public String execute(Packet packet) {
        String message = packet.getBMsq().getMessageString();
        Group readGroup = groupDAO.readGroup(Integer.parseInt(message));
        if (readGroup != null) {
            return readGroup.toJSON().toString();
        } else {
            return "Incorrect id of group";
        }
    }
}

