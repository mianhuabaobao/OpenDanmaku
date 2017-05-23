package com.opendanmaku.codec;

public class MessageRequest extends MessageProtocol {

	public MessageRequest(byte length, byte version, byte type, byte channel, byte[] message, short checksum) {
		super(length, version, type, channel, message, checksum);
		// TODO Auto-generated constructor stub
	}

}
