package org.example.packetProcessing.strategies;

import org.example.database.interfaces.IItemDAO;
import org.example.packet.Message.cTypes;
import org.example.database.interfaces.IGroupDAO;
import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.packetProcessing.strategies.group.AddGroupStrategy;
import org.example.packetProcessing.strategies.group.DeleteGroupStrategy;
import org.example.packetProcessing.strategies.group.ReadGroupStrategy;
import org.example.packetProcessing.strategies.group.UpdateGroupStrategy;
import org.example.packetProcessing.strategies.item.AddItemStrategy;
import org.example.packetProcessing.strategies.item.DeleteItemStrategy;
import org.example.packetProcessing.strategies.item.ReadItemStrategy;
import org.example.packetProcessing.strategies.item.UpdateItemStrategy;

import java.util.HashMap;
import java.util.Map;

public class CommandStrategyFactory {
    private final Map<cTypes, CommandStrategy> strategies = new HashMap<>();

    public CommandStrategyFactory(IItemDAO itemDAO, IGroupDAO groupDAO) {
        strategies.put(cTypes.READ_ITEM, new ReadItemStrategy(itemDAO));
        strategies.put(cTypes.ADD_ITEM, new AddItemStrategy(itemDAO));
        strategies.put(cTypes.UPDATE_ITEM, new UpdateItemStrategy(itemDAO));
        strategies.put(cTypes.DELETE_ITEM, new DeleteItemStrategy(itemDAO));
        strategies.put(cTypes.READ_GROUP, new ReadGroupStrategy(groupDAO));
        strategies.put(cTypes.ADD_GROUP, new AddGroupStrategy(groupDAO));
        strategies.put(cTypes.UPDATE_GROUP, new UpdateGroupStrategy(groupDAO));
        strategies.put(cTypes.DELETE_GROUP, new DeleteGroupStrategy(groupDAO, itemDAO));
    }

    public CommandStrategy getStrategy(cTypes commandType) {
        return strategies.get(commandType);
    }
}
