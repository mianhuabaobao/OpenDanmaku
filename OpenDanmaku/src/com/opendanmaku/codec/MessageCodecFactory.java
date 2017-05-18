package com.opendanmaku.codec;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.core.session.IoSession;

public class MessageCodecFactory implements ProtocolCodecFactory {
    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;

    public MessageCodecFactory(boolean client) {
        if (client) {
            encoder = new MessageRequestEncoder();
            decoder = new MessageResponseDecoder();
        } else {
            encoder = new MessageResponseEncoder();
            decoder = new MessageRequestDecoder();
        }
    }
    
    public MessageCodecFactory() {
    	this(false);
    }

    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}
