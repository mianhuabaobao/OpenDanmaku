package com.opendanmaku.codec;

public class MessageResponse extends MessageProtocol {

	public MessageResponse(byte length, byte version, byte type, byte channel, byte[] message, short checksum) {
		super(length, version, type, channel, message, checksum);
		// TODO Auto-generated constructor stub
	}

}
