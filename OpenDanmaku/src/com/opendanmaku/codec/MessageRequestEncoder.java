package com.opendanmaku.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.opendanmaku.util.MessageConstants;

public class MessageRequestEncoder implements ProtocolEncoder {

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
    	MessageRequest request = (MessageRequest) message;
    	byte type = request.getType();
    	int capacity = (request.getLength() & 0x0FF) + 1;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.put(request.getLength());
        buffer.put(request.getVersion());
        buffer.put(type);
        buffer.put(request.getChannel());
        if (type == MessageConstants.MESSAGE_BROADCAST) {
        	buffer.put(request.getMessage());
        }
        buffer.putShort(request.getChecksum());
        buffer.flip();
        out.write(buffer);
    }
	
	@Override
	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
        // nothing to dispose
	}
	
}
