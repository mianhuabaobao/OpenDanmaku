package com.opendanmaku.main;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.opendanmaku.codec.MessageProtocol;
import com.opendanmaku.util.MessageConstants;

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
	
	//0x06|0xff|0x00|0x00|0x01|0x0000
	public static final MessageProtocol KEEPALIVE_REQUEST = new MessageProtocol((byte)0x06, MessageConstants.MESSAGE_VERSION, MessageConstants.MESSAGE_KEEPALIVE, MessageConstants.BROADCAST_CHANNEL, new byte[]{1}, (short)0);
	//0x06|0xff|0x00|0x00|0x02|0x0000
	public static final MessageProtocol KEEPALIVE_RESPONSE = new MessageProtocol((byte)0x06, MessageConstants.MESSAGE_VERSION, MessageConstants.MESSAGE_KEEPALIVE, MessageConstants.BROADCAST_CHANNEL, new byte[]{2}, (short)0);
	
	@Override
	public Object getRequest(IoSession session) {
		return null;
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		return KEEPALIVE_RESPONSE;
	}

	@Override
	public boolean isRequest(IoSession session, Object message) {
		MessageProtocol msg = (MessageProtocol) message;
		if (msg.getType() == MessageConstants.MESSAGE_KEEPALIVE) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		return false;
	}

}
