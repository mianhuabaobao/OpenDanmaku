package com.opendanmaku.main;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DanmakuProtocolHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(DanmakuProtocolHandler.class);
    
    private AtomicInteger mClients = new AtomicInteger(0);
    
    @Override
    public void sessionCreated(IoSession session) {
        //session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 15);

        // We're going to use SSL negotiation notification.
        session.setAttribute(SslFilter.USE_NOTIFICATION);
        int clients = mClients.incrementAndGet();
        System.out.println("sessionCreated >>> " + clients);
    }
    
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LOGGER.info("OPENED");
        System.out.println("sessionOpened");        
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        LOGGER.info("CLOSED");
        int clients = mClients.decrementAndGet();
        System.out.println("sessionClosed >>> " + clients);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        LOGGER.info("*** IDLE #" + session.getIdleCount(IdleStatus.READER_IDLE) + " ***");
        System.out.println("*** sessionIdle #" + session + " #" + session.getIdleCount(IdleStatus.READER_IDLE) + " ***");
        //session.closeNow();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
    	cause.printStackTrace();
        session.closeNow();
        System.out.println("exceptionCaught"); 
    }

    private int count = 0;
    
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        System.out.println("messageReceived : count = " + (++count) + " | " + message);
        // Write the received data back to remote peer
        //session.write(((IoBuffer) message).duplicate());
        session.write(message);
    }
}