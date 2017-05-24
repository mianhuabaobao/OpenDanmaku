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
			
			MessageProtocol response = null;
			short checksum = 0;
			
			switch (type) {
			case MessageConstants.MESSAGE_KEEPALIVE:
			case MessageConstants.MESSAGE_SUBSCRIBE:
			case MessageConstants.MESSAGE_UNSUBSCRIBE:
			case MessageConstants.MESSAGE_BROADCAST:
				byte[] message = null;
				int len = (length & 0x0FF) - 5;
				if (len > 0) {
					message = new byte[len];
					in.get(message);
				}
				checksum = in.getShort();
				response = new MessageProtocol(length, version, type, channel, message, checksum);						
				break;					
			default:
				throw new Exception("Unknown MessageRequest's type.");
				//break;
			}

			out.write(response);
			return true;
		}
		
		return false;
	}
 
}
