package org.example.packetProcessing;

import org.example.database.DBGroup;
import org.example.database.DBItem;
import org.example.packet.Message;
import org.example.packet.Packet;
import org.example.warehouse.Group;
import org.example.warehouse.Item;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class Processor {

    public static final String dbFile = "data.db";

    public static byte[] processPacketAndGetAnswer(Packet cur_packet) {
        String message = cur_packet.getBMsq().getMessageString();
        int commandType = cur_packet.getBMsq().getCType();
        Message.cTypes[] commands = Message.cTypes.values();
        Message.cTypes command = commands[commandType];
        DBItem dbItem;
        Item item;
        DBGroup dbGroup;
        Group group;
        JSONObject info;
        String answer = "OK";
        switch (command) {
            case READ_ITEM:
                dbItem = new DBItem(dbFile);
                Item readItem = dbItem.readItem(Integer.parseInt(message));
                if (readItem != null) {
                    answer = readItem.toJSON().toString();
                } else {
                    answer = "Incorrect id of item";
                }
                break;
            case ADD_ITEM:
                info = new JSONObject(message);
                item = new Item(info.getInt("id"),
                        info.getString("name"),
                        info.getString("description"),
                        info.getString("supplier"),
                        info.getInt("stock_qty"),
                        info.getDouble("price"),
                        info.getInt("group_id"));
                dbItem = new DBItem(dbFile);
                if (dbItem.addItem(item) == -1) {
                    answer = "Not unique name of item";
                } else {
                    answer = "Successfully added item";
                }
                break;
            case UPDATE_ITEM:
                info = new JSONObject(message);
                item = new Item(info.getInt("id"),
                        info.getString("name"),
                        info.getString("description"),
                        info.getString("supplier"),
                        info.getInt("stock_qty"),
                        info.getDouble("price"),
                        info.getInt("group_id"));
                dbItem = new DBItem(dbFile);
                int updatedItem = dbItem.updateItem(item);
                if (updatedItem == -1) {
                    answer = "Incorrect id of item";
                } else if (updatedItem == -2) {
                    answer = "Not unique name of item";
                } else {
                    answer = "Successfully updated item";
                }
                break;
            case DELETE_ITEM:
                dbItem = new DBItem(dbFile);
                int item_id = dbItem.deleteItem(Integer.parseInt(message));
                if (item_id < 0) {
                    answer = "Incorrect id of item";
                } else {
                    answer = "Successfully deleted item";
                }
                break;
            case READ_GROUP:
                dbGroup = new DBGroup(dbFile);
                Group readGroup = dbGroup.readGroup(Integer.parseInt(message));
                if (readGroup != null) {
                    answer = readGroup.toJSON().toString();
                } else {
                    answer = "Incorrect id of group";
                }
                break;
            case ADD_GROUP:
                info = new JSONObject(message);
                group = new Group(info.getInt("id"),
                        info.getString("name"),
                        info.getString("description"));
                dbGroup = new DBGroup(dbFile);
                if (dbGroup.addGroup(group) == -1) {
                    answer = "Not unique name of group";
                } else {
                    answer = "Successfully added group";
                }
                break;
            case UPDATE_GROUP:
                info = new JSONObject(message);
                group = new Group(info.getInt("id"),
                        info.getString("name"),
                        info.getString("description"));
                dbGroup = new DBGroup(dbFile);
                int updatedGroup = dbGroup.updateGroup(group);
                if (updatedGroup == -1) {
                    answer = "Incorrect id of group";
                } else if(updatedGroup == -2) {
                    answer = "Not unique name of group";
                } else {
                    answer = "Successfully updated group";
                }
                break;
            case DELETE_GROUP:
                dbGroup = new DBGroup(dbFile);
                int group_id = dbGroup.deleteGroup(Integer.parseInt(message));
                if (group_id < 0) {
                    answer = "Incorrect id of group";
                } else {
                    dbItem = new DBItem(dbFile);
                    // delete all items in this group
                    dbItem.deleteAllItemsInGroup(group_id);
                    answer = "Successfully deleted group";
                }
                break;
        }
        System.out.println("Message from client: " + message + "\nclient ID: " + cur_packet.getBSrc() + "\npacket ID: " + cur_packet.getBPktId());
        Message answerMessage = new Message(cur_packet.getBMsq().getCType(), cur_packet.getBMsq().getBUserId(), answer.getBytes(StandardCharsets.UTF_8));
        Packet answerPacket = new Packet(cur_packet.getBSrc(), cur_packet.getBPktId(), answerMessage);
        return answerPacket.toPacket();
    }
}