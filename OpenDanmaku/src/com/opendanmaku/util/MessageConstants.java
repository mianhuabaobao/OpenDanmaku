package com.opendanmaku.util;

public class MessageConstants {
	
	public static final int LENGTH_HEADER = 1;
	
	public static final byte MESSAGE_VERSION = (byte) 0xff;
	public static final byte MESSAGE_CHECKSUM = (byte) 0x0;
	
	public static final byte BROADCAST_CHANNEL = 0;

	//
	public static final byte MESSAGE_KEEPALIVE = 0;
	public static final byte MESSAGE_SUBSCRIBE = 1;
	public static final byte MESSAGE_BROADCAST = 2;

}
