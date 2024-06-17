package org.example.packetProcessing.strategies;

import org.example.packet.Packet;

public interface CommandStrategy {
    String execute(Packet packet);
}