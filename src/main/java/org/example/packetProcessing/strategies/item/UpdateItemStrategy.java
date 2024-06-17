package org.example.packetProcessing.strategies.item;

import org.example.database.interfaces.IItemDAO;
import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.warehouse.Item;
import org.json.JSONObject;
import org.example.packet.Packet;

public class UpdateItemStrategy implements CommandStrategy {
    private final IItemDAO itemDAO;

    public UpdateItemStrategy(IItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public String execute(Packet packet) {
        String message = packet.getBMsq().getMessageString();
        JSONObject info = new JSONObject(message);
        Item item = new Item(info.getInt("id"),
                info.getString("name"),
                info.getString("description"),
                info.getString("supplier"),
                info.getInt("stock_qty"),
                info.getDouble("price"),
                info.getInt("group_id"));
        int updatedItem = itemDAO.updateItem(item);
        if (updatedItem == -1) {
            return "Incorrect id of item";
        } else if (updatedItem == -2) {
            return "Not unique name of item";
        } else {
            return "Successfully updated item";
        }
    }
}
