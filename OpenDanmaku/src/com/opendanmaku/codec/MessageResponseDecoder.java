package com.opendanmaku.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.opendanmaku.util.MessageConstants;

public class MessageResponseDecoder extends CumulativeProtocolDecoder {
	
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if (in.prefixedDataAvailable(1)) {
			byte length = in.get();
			byte version = in.get();
			byte type = in.get();
			byte channel = in.get();
			
			MessageRequest request = null;
			short checksum = 0;
			
			switch (type) {
			case MessageConstants.MESSAGE_SUBSCRIBE:
				checksum = in.getShort();
				request = new MessageRequest(length, version, type, channel, checksum);					
				break;
			case MessageConstants.MESSAGE_BROADCAST:
				byte[] message = new byte[(length & 0x0FF) - 5];
				in.get(message);
				checksum = in.getShort();
				request = new MessageRequest(length, version, type, channel, message, checksum);						
				break;					
			default:
				throw new Exception("Unknown MessageRequest's type.");
				//break;
			}

			out.write(request);
			return true;
		}
		
		return false;
	}
 
}
