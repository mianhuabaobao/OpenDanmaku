package com.opendanmaku.util;

public class MessageUtils {
	
	public static String getHexString(byte[] buffer) {
		return getHexString(buffer, 0, buffer.length);
	}
	
	public static String getHexString(byte[] buffer, int offset, int count) {
		StringBuilder buf = new StringBuilder();
		for (int i = offset; i < count; i++) {
			if ((buffer[i] & 0xff) < 0x10)
				buf.append("0");
			buf.append(Integer.toHexString((buffer[i] & 0xff))).append(" ");
		}
		return buf.toString();
	}
}
