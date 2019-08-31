package com.lightning.opendisasm.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.nio.charset.Charset;

import javax.xml.bind.helpers.ValidationEventImpl;

public class BytewiseReader implements Closeable {
    private InputStream stream;
    private boolean bigEndian;
    private long pos;
    
    public byte[] readBytes(int len) throws IOException {
    	byte[] ret = new byte[len];
    	if(stream.read(ret)!=len)
    		throw new EOFException("Unexpected End of Stream");
    	return ret;
    }
    
    private int read() throws IOException {
    	int val = stream.read();
    	if(val<0)
    		throw new EOFException("Reached end of stream");
    	pos++;
    	return val;
    }
    
    public BytewiseReader(InputStream stream) {
        this.stream = stream;
    }
    
    public BytewiseReader(InputStream stream,boolean bigEndian) {
        this.stream = stream;
        this.bigEndian = bigEndian;
    }
    
    public int readMagicInt(int length) throws IOException {
        assert length <= 4&&length>0;
        switch(length) {
        case 1:
        	return read();
        case 2:
        	return read()<<8 | read();
        case 3:
        	return read()<<16 | read()<<8 | read();
        case 4:
        	return read()<<24 | read()<<16 | read()<<8 | read();
        default:
        	throw new IllegalArgumentException("length must be between 1 and 4 inclusive");
        }
    }
    
    public short readUByte() throws IOException {
        return (short) read();
    }
    
    public int readUShort() throws IOException {
        if(bigEndian) {
            return ((0x00FF & read()) << 8) + (0x00FF & read());
        } else {
            return (0x00FF & read()) + ((0x00FF & read()) << 8);
        }
    }
    
    public long readUInt() throws IOException {
        if(bigEndian) {
            return  ((0x00FFL & read()) << 24) + ((0x00FF  & read()) << 16) +
                    ((0x00FF  & read()) <<  8) + ((0x00FF  & read())      );
        } else {
            return  ((0x00FF  & read())      ) + ((0x00FF  & read()) <<  8) +
                    ((0x00FF  & read()) << 16) + ((0x00FFL & read()) << 24);
        }
    }
    
    public long readULong() throws IOException {
        if(bigEndian) {
            return  ((0x00FF & read()) << 56L) + ((0x00FF & read()) << 48L) +
                    ((0x00FF & read()) << 40L) + ((0x00FF & read()) << 32L) +
                    ((0x00FF & read()) << 24L) + ((0x00FF & read()) << 16L) +
                    ((0x00FF & read()) <<  8L) + ((0x00FF & read())       );
        } else {
            return  ((0x00FF & read())       ) + ((0x00FF & read()) << 8L ) +
                    ((0x00FF & read()) << 16L) + ((0x00FF & read()) << 24L) +
                    ((0x00FF & read()) << 32L) + ((0x00FF & read()) << 40L) +
                    ((0x00FF & read()) << 48L) + ((0x00FF & read()) << 56L);
        }
    }
    
    public void setBigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }
    
    public void skip(long numBytes) throws IOException {
        long bytesToSkip = numBytes;
        int numTries = 0;
        while(bytesToSkip != 0 && numTries++ < 0x10) { // Convoluted statement, I know.
            long bytesSkipped = stream.skip(bytesToSkip);
            bytesToSkip -= bytesSkipped;
            pos += bytesSkipped;
        }
    }
    
    public void skipTo(long newPos) throws IOException {
        if(newPos == pos)
            return;
        if(newPos < pos) {
            // TODO: Implement backwards skipping
            throw new ArrayIndexOutOfBoundsException("Can't go backwards in a stream! (yet)");
        } else {
            skip(newPos - pos);
        }
    }

    public void close() throws IOException {
        stream.close();
    }
    
    public static enum StringType{
    	LengthPrefixed {
			@Override
			byte[] readBytes(BytewiseReader in) throws IOException {
				int len = in.readUShort();
				return in.readBytes(len);
			}
		}, NulTerminated {
			@Override
			byte[] readBytes(BytewiseReader in) {
				// TODO Implement Reading Strings Terminated by a NUL (0x00) byte.
				return null;
			}
		};
    	
    	abstract byte[] readBytes(BytewiseReader in)throws IOException;
    }

	public String readString(Charset set) throws IOException {
		// TODO Auto-generated method stub
		return readString(set,StringType.LengthPrefixed);
	}

	public String readString(Charset set, StringType type) throws IOException {
		byte[] bytes = type.readBytes(this);
		return new String(bytes,set);
	}

	public int readInt() throws IOException {
		// TODO Auto-generated method stub
		return (int)readUInt();
	}
	
	public float readFloat() throws IOException{
		return Float.intBitsToFloat(readInt());
	}
	
	private <E extends Enum<E>> E valueOfIndex(Class<E> cl,int idx) {
		E[] e = cl.getEnumConstants();
		return e[idx];
	}
	
	public <E extends Enum<E>> E readEnum8(Class<E> cl) throws IOException {
		return valueOfIndex(cl,readUByte());
	}
	public <E extends Enum<E>> E readEnum16(Class<E> cl) throws IOException {
		return valueOfIndex(cl,readUShort());
	}
}
