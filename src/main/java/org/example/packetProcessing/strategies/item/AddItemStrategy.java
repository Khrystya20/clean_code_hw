package org.example.packetProcessing.strategies.item;

import org.example.database.interfaces.IItemDAO;
import org.example.packet.Packet;
import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.warehouse.Item;
import org.json.JSONObject;

public class AddItemStrategy implements CommandStrategy {
    private final IItemDAO itemDAO;

    public AddItemStrategy(IItemDAO itemDAO) {
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
        if (itemDAO.addItem(item) == -1) {
            return "Not unique name of item";
        } else {
            return "Successfully added item";
        }
    }
}