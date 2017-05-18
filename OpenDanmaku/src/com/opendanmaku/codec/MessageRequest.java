package com.opendanmaku.codec;

import com.opendanmaku.util.MessageConstants;
import com.opendanmaku.util.MessageUtils;

public class MessageRequest {

    private byte length;
    private byte version;
    private byte type;
    private byte channel;
    private byte[] message;
    private short checksum;

    // MESSAGE_SUBSCRIBE
    public MessageRequest(byte length, byte version, byte type, byte channel, short checksum) {
        this.length = length;
        this.version = version;
        this.type = type;
        this.channel = channel;
        this.checksum = checksum;
    }
    
    // MESSAGE_BROADCAST
    public MessageRequest(byte length, byte version, byte type, byte channel, byte[] message, short checksum) {
        this.length = length;
        this.version = version;
        this.type = type;
        this.channel = channel;
        this.message = message;
        this.checksum = checksum;
    }
    
    public byte getLength() {
        return length;
    }
    
    public byte getVersion() {
        return version;
    }

    public byte getType() {
        return type;
    }
    
    public byte getChannel() {
        return channel;
    }
    
    public byte[] getMessage() {
        return message;
    }    
    
    public short getChecksum() {
        return checksum;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("length: ").append(length & 0x0FF)
	    	.append(", version: ").append(version & 0x0FF)
	    	.append(", type: ").append(type & 0x0FF)
	    	.append(", channel: ").append(channel & 0x0FF);
    	
    	if (type == MessageConstants.MESSAGE_BROADCAST) {
	    	sb.append(", message: ").append(MessageUtils.getHexString(message));
    	}
    	
	    sb.append(", checksum: ").append(checksum);
	    
    	return sb.toString();
    }
}
