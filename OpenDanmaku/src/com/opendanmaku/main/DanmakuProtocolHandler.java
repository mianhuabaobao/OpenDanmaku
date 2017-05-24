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

import com.opendanmaku.codec.MessageProtocol;
import com.opendanmaku.util.MessageConstants;

public class DanmakuProtocolHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(DanmakuProtocolHandler.class);
    
    private final Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
    private final Set<String> users = Collections.synchronizedSet(new HashSet<String>());
    private final BlockingQueue<MessageProtocol> queues = new LinkedBlockingQueue<MessageProtocol>(102400);
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

				MessageProtocol response = null;
				
				for(;;) {
					try {
						response = (MessageProtocol) queues.take();
						broadcast(response);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error(e.toString());
					}
				}
			}
    	}, "BroadcastingThread").start();
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
        sessions.remove(session);
        LOGGER.info("sessionClosed(): " + sessions.size());
        doExit(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
    	LOGGER.info("sessionIdle(): " + session + " #" + session.getIdleCount(IdleStatus.READER_IDLE));
        session.closeNow();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        LOGGER.warn("Unexpected exception.", cause);
        //cause.printStackTrace();
        session.closeNow();
    }
    
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	//session.write(message);
    	MessageProtocol request = (MessageProtocol) message;
    	
        switch (request.getType()) {
        case MessageConstants.MESSAGE_SUBSCRIBE:
        	doSubscribe(session, request);
        	break;
        case MessageConstants.MESSAGE_UNSUBSCRIBE:
        	doUnsubscribe(session, request);
        	break;
        case MessageConstants.MESSAGE_BROADCAST:
        	doBroadcast(session, request);
        	break;
        default:
        	break;
        }

    }
    
    private void doBroadcast(IoSession session, MessageProtocol message) throws Exception {
    	queues.put(message);
    }
    
    private void doUnsubscribe(IoSession session, MessageProtocol request) throws Exception {
    	//REQUEST: 0x05|0xff|0x02|0x00|0x0000
    	// 05 ff 02 00 00 00
    	
    	doExit(session);
    }
    
    private void doExit(IoSession session) {
    	Object attr = null;

    	// step 1
    	attr = session.getAttribute(MessageConstants.KEY_USERNAME);
    	if (attr  != null) {
	    	String user = (String) attr;
	    	if (user != null) {
		    	users.remove(user);
		    	LOGGER.info("exit(" + user + "), user left:  " + users.size());
	    	}
    	}

    	// step 2
    	attr = session.getAttribute(MessageConstants.KEY_CHANNEL);
    	if (attr  != null) {
	    	int channel = (int) attr;
	    	Set<IoSession> receivers = channels.get(channel);
	    	if (receivers != null) {
		    	receivers.remove(session);
		    	LOGGER.info("exit(" + channel + "), receivers left: " + receivers.size());
	    	}
    	}
    }
    
    private void doSubscribe(IoSession session, MessageProtocol request) throws Exception {
    	//REQUEST: length|0xff|0x01|channel|username|0x0000
    	// 08 ff 01 01 61 62 63 00 00
    	
    	// step 1
    	byte[] msg = request.getMessage();
    	
    	if (msg == null) {
    		// 用户名不能为空: 06 ff 01 00 01 00 00
    		LOGGER.warn("username required.");
    		doResponse(session, new MessageProtocol((byte) 0x06, MessageConstants.MESSAGE_VERSION, MessageConstants.MESSAGE_SUBSCRIBE, (byte) 0, new byte[]{1}, (short) 0));
    		return;
    	} 

		String user = new String(msg, MessageConstants.CHARSET_UTF8);
		
		// step 2
		int channel = request.getChannel();
		Set<IoSession> receivers = channels.get(channel);
		
		if (receivers == null) {
			// 订阅频道不存在: 06 ff 01 00 02 00 00
			LOGGER.warn("channel not found: " + channel);
			doResponse(session, new MessageProtocol((byte) 0x06, MessageConstants.MESSAGE_VERSION, MessageConstants.MESSAGE_SUBSCRIBE, (byte) 0, new byte[]{2}, (short) 0));
			return;
		}
    	
		// step 3
		synchronized (users) {
        	if (users.contains(user)) {
        		// 用户名已存在: 06 ff 01 00 03 00 00
        		LOGGER.warn("user found: " + user);
        		doResponse(session, new MessageProtocol((byte) 0x06, MessageConstants.MESSAGE_VERSION, MessageConstants.MESSAGE_SUBSCRIBE, (byte) 0, new byte[]{3}, (short) 0));
        		return;
        	} else {
        		users.add(user);
        	}
		}
		
		// step 4
		session.setAttribute(MessageConstants.KEY_USERNAME, user);
		session.setAttribute(MessageConstants.KEY_CHANNEL, channel);
		receivers.add(session);
		
		LOGGER.info("doSubscribe: user = " + user + ", users = "+ users.size() + ", channel = " + channel + ", receivers = " + receivers.size());
		// 返回订阅成功命令: 06 ff 01 00 00 00 00
		doResponse(session, new MessageProtocol((byte) 0x06, MessageConstants.MESSAGE_VERSION, MessageConstants.MESSAGE_SUBSCRIBE, (byte) 0, new byte[]{0}, (short) 0));
    }
    
    private void doResponse(IoSession session, MessageProtocol response) {
    	session.write(response);
    }
    
    public void broadcast(MessageProtocol message) {
    	int channel = message.getChannel() & 0x0ff;
    	Set<IoSession> receivers = (channel == 0) ? sessions : channels.get(channel);
    	if (receivers != null) {
	        synchronized (receivers) {
	        	LOGGER.info("broadcasting " + channel + "|" + receivers.size() + " " + message);
	            for (IoSession session : receivers) {
	                if (session.isConnected()) {
	                    session.write(message);
	                }
	            }
	        } 
    	} else {
    		LOGGER.warn("receivers not found: " + channel);
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