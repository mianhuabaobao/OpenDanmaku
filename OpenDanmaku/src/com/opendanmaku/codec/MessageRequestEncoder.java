package com.opendanmaku.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MessageRequestEncoder implements ProtocolEncoder {

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
    	MessageProtocol request = (MessageProtocol) message;
    	int capacity = (request.getLength() & 0x0FF) + 1;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.put(request.getLength());
        buffer.put(request.getVersion());
        buffer.put(request.getType());
        buffer.put(request.getChannel());
        putMessage(buffer, request);
        buffer.putShort(request.getChecksum());
        buffer.flip();
        out.write(buffer);
    }
    
    private void putMessage(IoBuffer buffer, MessageProtocol request) {
        byte[] message = request.getMessage();
        if (message != null) {
        	buffer.put(message);
        }
    }
	
	@Override
	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
        // nothing to dispose
	}
	
}
