package org.example.packet;
import lombok.Data;
import org.example.util.MyCipher;

import java.nio.ByteBuffer;

@Data
public class Message {

    // big-endian command code
    Integer cType;
    // id of user from whom the message was sent
    Integer bUserId;
    // the message in byte array
    byte[] message;

    public enum cTypes {
        READ_ITEM,
        ADD_ITEM,
        UPDATE_ITEM,
        DELETE_ITEM,
        LIST_BY_CRITERIA_ITEMS,
        READ_GROUP,
        ADD_GROUP,
        UPDATE_GROUP,
        DELETE_GROUP,
        LIST_BY_CRITERIA_GROUPS
    }

    public static final int BYTES_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;

    public Message(Integer cType, Integer bUserId, String message){
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message.getBytes();
    }

    public Message(Integer cType, Integer bUserId, byte[] message){
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    public byte[] toPacketPart(){
        return ByteBuffer.allocate(getMessageBytesLength())
                .putInt(cType)
                .putInt(bUserId)
                .put(message)
                .array();
    }

    public int getMessageBytesLength(){
        return BYTES_WITHOUT_MESSAGE + getMessageLength();
    }

    public int getMessageLength(){
        return message.length;
    }

    public void encode(){
        message = MyCipher.encode(message);
    }

    public void decode(){
        message = MyCipher.decode(message);
    }

    public String getMessageString(){
        return new String (message);
    }

}