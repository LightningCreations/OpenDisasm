package com.lightning.opendisasm.disasm.clazz;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public class ModifiedUtf8Charset extends Charset {
	
	public static final ModifiedUtf8Charset modified = new ModifiedUtf8Charset("utf8java",new String[]{});
	
	
	private static class Decoder extends CharsetDecoder{

		protected Decoder(Charset cs) {
			super(cs, 0.5f, 1f);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
			while(in.hasRemaining()&&out.hasRemaining()) {
				byte a = in.get();
				if(((a&0x80)==0)&&a!=0)//This is a 1-byte sequence, so just push it zero-extended to a char
					out.append((char)a);
				else if((a&0b11100000)==0b11000000) {
					if(!in.hasRemaining())
						return CoderResult.malformedForLength(2);
					byte b = in.get();
					if((b&0b11000000)!=0b10000000)
						return CoderResult.malformedForLength(2);
					out.append((char)(((a& 0x1F) << 6) | (b & 0x3F)));
				}else if((a&0xF0)==0xE0) {
					if(!in.hasRemaining())
						return CoderResult.malformedForLength(3);
					byte b = in.get();
					if((b&0b11000000)!=0b10000000)
						return CoderResult.malformedForLength(3);
					if(!in.hasRemaining())
						return CoderResult.malformedForLength(3);
					byte c = in.get();
					if((c&0b11000000)!=0b10000000)
						return CoderResult.malformedForLength(3);
					out.append((char)(((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F)));
				}else
					return CoderResult.malformedForLength(1);
			}
			if(!out.hasRemaining())
				return CoderResult.OVERFLOW;
			return CoderResult.UNDERFLOW;
		}
		
	}
	
	public ModifiedUtf8Charset(String canonicalName, String[] aliases) {
		super(canonicalName, aliases);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean contains(Charset cs) {
		// TODO Auto-generated method stub
		return StandardCharsets.UTF_8.contains(cs);
	}

	@Override
	public CharsetDecoder newDecoder() {
		// TODO Auto-generated method stub
		return new Decoder(this);
	}
	
	@Override
	public CharsetEncoder newEncoder() {
		// TODO Auto-generated method stub
		return null;
	}

}
