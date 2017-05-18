package com.opendanmaku.main;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DanmakuProtocolHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(DanmakuProtocolHandler.class);
    
    private final Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
    private final Set<String> users = Collections.synchronizedSet(new HashSet<String>());
    
    @Override
    public void sessionCreated(IoSession session) {

    }
    
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    	sessions.add(session);
    	LOGGER.debug("sessionOpened(): " + sessions.size());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        //String user = (String) session.getAttribute(MessageConstants.KEY_USERNAME);
        //users.remove(user);
        sessions.remove(session);
        //broadcast();
        LOGGER.debug("sessionClosed(): " + sessions.size());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
    	LOGGER.debug("sessionIdle(): " + session + " #" + session.getIdleCount(IdleStatus.READER_IDLE));
        session.closeNow();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        LOGGER.warn("Unexpected exception.", cause);
        cause.printStackTrace();
        session.closeNow();
    }
    
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	session.write(message);
    	
//    	MessageRequest request = (MessageRequest) message;
//
//    	String user = (String) session.getAttribute(MessageConstants.KEY_USERNAME);
//    	
//        switch (request.getType()) {
//        case MessageConstants.MESSAGE_SUBSCRIBE:
//        	
//        	break;
//        case MessageConstants.MESSAGE_BROADCAST:
//        	
//        	break;
//        default:
//        	break;
//        }

    }
    
    public void broadcast(String message) {
        synchronized (sessions) {
            for (IoSession session : sessions) {
                if (session.isConnected()) {
                    session.write("BROADCAST OK " + message);
                }
            }
        }
    }

    public void kick(String name) {
        synchronized (sessions) {
            for (IoSession session : sessions) {
                if (name.equals(session.getAttribute("user"))) {
                    session.closeNow();
                    break;
                }
            }
        }
    }
    
    public int getUsersSize() {
        return users.size();
    }
}