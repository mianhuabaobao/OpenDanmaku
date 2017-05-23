package com.opendanmaku.main;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendanmaku.codec.MessageRequest;

public class DanmakuProtocolHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(DanmakuProtocolHandler.class);
    
    private final Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
    private final Set<String> users = Collections.synchronizedSet(new HashSet<String>());
    private final BlockingQueue<MessageRequest> queues = new LinkedBlockingQueue<MessageRequest>(4096);
    private final Map<Integer, Set<IoSession>> channels =  new HashMap<Integer, Set<IoSession>>();
    
    public DanmakuProtocolHandler() {
    	super();    	
    	setupChannels();
    	broadcasting();
    }
    
    private void setupChannels() {
    	channels.put(1, Collections.synchronizedSet(new HashSet<IoSession>()));
    	channels.put(2, Collections.synchronizedSet(new HashSet<IoSession>()));
    }
    
    private void broadcasting() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("broadcasting now.");

				MessageRequest request = null;
				
				for(;;) {
					try {
						request = queues.take();
						broadcast(request);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error(e.toString());
					}
				}
			}
    	}).start();
    }
    
    @Override
    public void sessionCreated(IoSession session) {
    	// TODO Auto-generated method stub
    }
    
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    	sessions.add(session);
    	LOGGER.info("sessionOpened(): " + sessions.size());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        //String user = (String) session.getAttribute(MessageConstants.KEY_USERNAME);
        //users.remove(user);
        sessions.remove(session);
        //broadcast();
        LOGGER.info("sessionClosed(): " + sessions.size());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
    	LOGGER.info("sessionIdle(): " + session + " #" + session.getIdleCount(IdleStatus.READER_IDLE));
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
    	//session.write(message);
    	queues.put((MessageRequest) message);

    	
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
    
    public void broadcast(MessageRequest message) {
    	int channel = message.getChannel() & 0x0ff;
    	
        synchronized (sessions) {
        	LOGGER.info("broadcasting#" + sessions.size() + " " + message);
            for (IoSession session : sessions) {
                if (session.isConnected()) {
                    session.write(message);
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