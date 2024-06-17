package org.example.packet;

import com.github.snksoft.crc.CRC;
import com.google.common.primitives.UnsignedLong;
import lombok.Data;
import java.nio.ByteBuffer;

@Data
public class Packet {
    // the byte indicating the beginning of the packet
    public final static Byte bMagic = 0x13;
    // unique client application number
    Byte bSrc;
    // message number (in big-endian format)
    UnsignedLong bPktId;
    // the length of the big-endian data packet
    Integer wLen;
    // message - useful information
    Message bMsq;
    // CRC16 bytes (00-13) big-endian
    Short wCrc16_1;
    // CRC16 bytes (16 to 16 + wLen-1) big-endian
    Short wCrc16_2;

    public Packet(Byte bSrc, UnsignedLong bPktId, Message bMsq){
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.bMsq = bMsq;
        this.wLen = bMsq.getMessageLength();
    }

    public Packet(byte[] encodedPacket) throws Exception{
        ByteBuffer buffer = ByteBuffer.wrap(encodedPacket);
        Byte expectedBMagic = buffer.get();
        if(!expectedBMagic.equals(bMagic)){
            throw new Exception("Unexpected bMagic");
        }
        bSrc = buffer.get();
        bPktId = UnsignedLong.fromLongBits(buffer.getLong());
        wLen = buffer.getInt();
        int packetPartFirstLength = bMagic.BYTES + bSrc.BYTES + Long.BYTES + wLen.BYTES;
        byte[] packetPartFirst = ByteBuffer.allocate(packetPartFirstLength)
                .put(bMagic).put(bSrc).putLong(bPktId.longValue()).putInt(wLen).array();
        // CRC16_1 validation
        Short calculated_wCrc16_1 = (short) CRC.calculateCRC(CRC.Parameters.CRC16, packetPartFirst);
        wCrc16_1 = buffer.getShort();
        if(!calculated_wCrc16_1.equals(wCrc16_1)){
            throw new Exception("Unexpected wCrc16_1");
        }
        // get parameters for message
        int cType = buffer.getInt();
        int bUserId = buffer.getInt();
        byte[] messageText = new byte[wLen];
        buffer.get(messageText);
        // initialize message
        bMsq = new Message(cType, bUserId, messageText);
        byte[] packetPartSecond = ByteBuffer.allocate(bMsq.getMessageBytesLength())
                .put(bMsq.toPacketPart()).array();
        // CRC16_2 validation
        Short calculated_wCrc16_2 = (short) CRC.calculateCRC(CRC.Parameters.CRC16, packetPartSecond);
        wCrc16_2 = buffer.getShort();
        if(!calculated_wCrc16_2.equals(wCrc16_2)){
            throw new Exception("Unexpected wCrc16_2");
        }
        bMsq.decode();
    }

    public byte[] toPacket(){
        Message message = getBMsq();
        message.encode();
        wLen = message.getMessageLength();
        int packetPartFirstLength = bMagic.BYTES + bSrc.BYTES + Long.BYTES + wLen.BYTES;
        byte[] packetPartFirst = ByteBuffer.allocate(packetPartFirstLength)
                .put(bMagic).put(bSrc).putLong(bPktId.longValue()).putInt(wLen).array();
        wCrc16_1 = (short) CRC.calculateCRC(CRC.Parameters.CRC16, packetPartFirst);
        byte[] packetPartSecond = ByteBuffer.allocate(bMsq.getMessageBytesLength())
                .put(bMsq.toPacketPart()).array();
        wCrc16_2 = (short) CRC.calculateCRC(CRC.Parameters.CRC16, packetPartSecond);
        int packetLength = packetPartFirstLength + wCrc16_1.BYTES + bMsq.getMessageBytesLength() + wCrc16_2.BYTES;
        return ByteBuffer.allocate(packetLength).put(packetPartFirst).putShort(wCrc16_1).put(packetPartSecond).putShort(wCrc16_2).array();
    }
}