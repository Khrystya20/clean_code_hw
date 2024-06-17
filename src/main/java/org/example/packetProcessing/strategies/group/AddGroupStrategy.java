package org.example.packetProcessing.strategies.group;

import org.example.database.interfaces.IGroupDAO;
import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.warehouse.Group;
import org.json.JSONObject;
import org.example.packet.Packet;

public class AddGroupStrategy implements CommandStrategy {
    private final IGroupDAO groupDAO;

    public AddGroupStrategy(IGroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }

    @Override
    public String execute(Packet packet) {
        String message = packet.getBMsq().getMessageString();
        JSONObject info = new JSONObject(message);
        Group group = new Group(info.getInt("id"),
                info.getString("name"),
                info.getString("description"));
        if (groupDAO.addGroup(group) == -1) {
            return "Not unique name of group";
        } else {
            return "Successfully added group";
        }
    }
}