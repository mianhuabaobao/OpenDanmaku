package com.opendanmaku.main;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendanmaku.codec.MessageCodecFactory;
import com.opendanmaku.util.DanmakuConfig;

public class DanmakuServer {

	private final static Logger LOGGER = LoggerFactory.getLogger(DanmakuProtocolHandler.class);
    
	public static void main(String[] args) throws Exception {

        SocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setReuseAddress(true);
        
        SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
        //sessionConfig.setReadBufferSize(1024);
		
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        //
        if (DanmakuConfig.USE_MESSAGECODEC) {
        	addMessageCodecSupport(chain);
        } else {
        	addTextLineCodecSupport(chain);
        	
        }
        
        if (DanmakuConfig.USE_KEEPALIVE) {
        	addKeepAliveSupport(chain);
        } else {
        	sessionConfig.setIdleTime(IdleStatus.READER_IDLE, DanmakuConfig.IDEL_TIMEOUT);
        }
        
        if (DanmakuConfig.USE_THREADPOOL) {
        	addThreadPoolSupport(chain);
        }
        
        // 
        acceptor.setHandler(new DanmakuProtocolHandler());
        acceptor.bind(new InetSocketAddress(DanmakuConfig.PORT));

        LOGGER.info("Danmaku's Server Listening on port " + DanmakuConfig.PORT);

    }

    private static void addKeepAliveSupport(DefaultIoFilterChainBuilder chain) throws Exception {
    	// KeepAlive: active, semi-active, passive, deaf speaker, sient-listener
        KeepAliveMessageFactory keepAliveMessageFactory = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(keepAliveMessageFactory, IdleStatus.READER_IDLE);  
        keepAliveFilter.setForwardEvent(true);
        keepAliveFilter.setRequestInterval(DanmakuConfig.KEEPALIVE_REQUEST_INTERVAL);
        //keepAliveFilter.setRequestTimeout(KEEPALIVE_REQUEST_TIMEOUT);
		chain.addLast("keepAliveFilter", keepAliveFilter);
    }
    
    private static void addMessageCodecSupport(DefaultIoFilterChainBuilder chain) throws Exception {
		chain.addLast("codec", new ProtocolCodecFilter(new MessageCodecFactory()));
    }
    
    private static void addTextLineCodecSupport(DefaultIoFilterChainBuilder chain) throws Exception {
    	chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
    }
    
    private static void addThreadPoolSupport(DefaultIoFilterChainBuilder chain) throws Exception {
    	chain.addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
    }

}
