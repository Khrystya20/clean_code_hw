package org.example.packetProcessing;

import org.example.database.DAOFactory;
import org.example.packet.Message;
import org.example.packet.Packet;
import org.example.packetProcessing.strategies.CommandStrategy;
import org.example.packetProcessing.strategies.CommandStrategyFactory;

import java.nio.charset.StandardCharsets;

public class Processor {

    private static final String dbFile = "data.db";
    private static final DAOFactory daoFactory = new DAOFactory(dbFile);
    private static final CommandStrategyFactory strategyFactory = new CommandStrategyFactory(
            daoFactory.createItemDAO(), daoFactory.createGroupDAO());

    public static byte[] processPacketAndGetAnswer(Packet cur_packet) {
        String message = cur_packet.getBMsq().getMessageString();
        int commandType = cur_packet.getBMsq().getCType();
        Message.cTypes[] commands = Message.cTypes.values();
        Message.cTypes command = commands[commandType];

        CommandStrategy strategy = strategyFactory.getStrategy(command);
        String answer = strategy.execute(cur_packet);

        System.out.println("Message from client: " + message + "\nclient ID: " + cur_packet.getBSrc() + "\npacket ID: " + cur_packet.getBPktId());
        Message answerMessage = new Message(cur_packet.getBMsq().getCType(), cur_packet.getBMsq().getBUserId(), answer.getBytes(StandardCharsets.UTF_8));
        Packet answerPacket = new Packet(cur_packet.getBSrc(), cur_packet.getBPktId(), answerMessage);
        return answerPacket.toPacket();
    }
}
