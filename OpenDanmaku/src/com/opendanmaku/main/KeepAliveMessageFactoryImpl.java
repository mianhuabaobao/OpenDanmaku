package com.opendanmaku.main;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

	public static final String HEARTBEAT_REQUEST = "a";  
	public static final String HEARTBEAT_RESPONSE = "b";  
	    
	@Override
	public Object getRequest(IoSession session) {
		System.out.println("getRequest");
		//return HEARTBEAT_REQUEST;
		return null;
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		System.out.println("getResponse >>>>>>>>>>>>>  request: " + request);
		return HEARTBEAT_RESPONSE;
		//return null;
	}

	@Override
	public boolean isRequest(IoSession session, Object message) {
		System.out.println("isRequest: " + message);
		if (message.equals(HEARTBEAT_REQUEST)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		System.out.println("isResponse: " + message);
//		if (message.equals(HEARTBEAT_RESPONSE)) {
//			return true;
//		}
		return false;
	}

}
