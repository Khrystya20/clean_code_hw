package org.example.packetProcessing.strategies.group;

import org.example.database.interfaces.IGroupDAO;
import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.warehouse.Group;
import org.json.JSONObject;
import org.example.packet.Packet;

public class UpdateGroupStrategy implements CommandStrategy {
    private final IGroupDAO groupDAO;

    public UpdateGroupStrategy(IGroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }

    @Override
    public String execute(Packet packet) {
        String message = packet.getBMsq().getMessageString();
        JSONObject info = new JSONObject(message);
        Group group = new Group(info.getInt("id"),
                info.getString("name"),
                info.getString("description"));
        int updatedGroup = groupDAO.updateGroup(group);
        if (updatedGroup == -1) {
            return "Incorrect id of group";
        } else if(updatedGroup == -2) {
            return "Not unique name of group";
        } else {
            return "Successfully updated group";
        }
    }
}
