package com.opendanmaku.main;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.opendanmaku.codec.MessageCodecFactory;

public class DanmakuServer {

	public static final int PORT = 80;
    
    public static final boolean USE_KEEPALIVE = true;
    public static final boolean USE_MESSAGECODEC = false;
    
    public static final int IDEL_TIMEOUT = 60;
    public static final int KEEPALIVE_REQUEST_INTERVAL = 5;
    
    public static void main(String[] args) throws Exception {
    	
        SocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setReuseAddress(true);
        
        SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
        sessionConfig.setReadBufferSize(1024);
		//sessionConfig.setIdleTime(IdleStatus.READER_IDLE, IDEL_TIMEOUT);
		
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        //
        if (USE_MESSAGECODEC) {
        	addMessageCodecSupport(chain);
        } else {
        	addTextLineCodecSupport(chain);
        	
        }
        
        if (USE_KEEPALIVE) {
        	addKeepAliveSupport(chain);
        }
        
        // Bind
        acceptor.setHandler(new DanmakuProtocolHandler());
        acceptor.bind(new InetSocketAddress(PORT));

        System.out.println("Listening on port " + PORT);

    }

    private static void addKeepAliveSupport(DefaultIoFilterChainBuilder chain) throws Exception {
        KeepAliveMessageFactory keepAliveMessageFactory = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(keepAliveMessageFactory, IdleStatus.READER_IDLE);  
        keepAliveFilter.setForwardEvent(true);
        keepAliveFilter.setRequestInterval(KEEPALIVE_REQUEST_INTERVAL);  
		chain.addLast("keepAliveFilter", keepAliveFilter);
    	
    }
    
    private static void addMessageCodecSupport(DefaultIoFilterChainBuilder chain) throws Exception {
		chain.addLast("codec", new ProtocolCodecFilter(new MessageCodecFactory()));
    }
    
    private static void addTextLineCodecSupport(DefaultIoFilterChainBuilder chain) throws Exception {
    	chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
    }

}
