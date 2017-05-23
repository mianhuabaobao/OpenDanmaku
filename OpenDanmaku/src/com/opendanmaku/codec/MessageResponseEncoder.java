package com.opendanmaku.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MessageResponseEncoder implements ProtocolEncoder {

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
    	MessageProtocol response = (MessageProtocol) message;
    	int capacity = (response.getLength() & 0x0FF) + 1;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.put(response.getLength());
        buffer.put(response.getVersion());
        buffer.put(response.getType());
        buffer.put(response.getChannel());
        putMessage(buffer, response);
        buffer.putShort(response.getChecksum());
        buffer.flip();
        out.write(buffer);
    }

    private void putMessage(IoBuffer buffer, MessageProtocol response) {
        byte[] message = response.getMessage();
        if (message != null) {
        	buffer.put(message);
        }
    }
    
    public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
        // nothing to dispose
    }
}
