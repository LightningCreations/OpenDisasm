package com.lightning.opendisasm.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BytewiseReader implements Closeable {
    private InputStream stream;
    private boolean bigEndian;
    
    private int read() throws IOException {
    	int val = stream.read();
    	if(val<0)
    		throw new EOFException("Reached end of stream");
    	return val;
    }
    
    public BytewiseReader(InputStream stream) {
        this.stream = stream;
    }
    
    public BytewiseReader(InputStream stream,boolean bigEndian) {
        this.stream = stream;
        this.bigEndian = bigEndian;
    }
    
    
    public int readMagic(int length) throws IOException {
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
                    ((0x00FF  & read()) <<  8) + ((0x00FF  & stream.read())      );
        } else {
            return  ((0x00FF  & read())      ) + ((0x00FF  & read()) <<  8) +
                    ((0x00FF  & read()) << 16) + ((0x00FFL & read()) << 24);
        }
    }
    
    public long readULong() throws IOException {
        if(bigEndian) {
            return  ((0x00FF & stream.read()) << 56L) + ((0x00FF & stream.read()) << 48L) +
                    ((0x00FF & stream.read()) << 40L) + ((0x00FF & stream.read()) << 32L) +
                    ((0x00FF & stream.read()) << 24L) + ((0x00FF & stream.read()) << 16L) +
                    ((0x00FF & stream.read()) <<  8L) + ((0x00FF & stream.read())       );
        } else {
            return  ((0x00FF & stream.read())       ) + ((0x00FF & stream.read()) << 8L ) +
                    ((0x00FF & stream.read()) << 16L) + ((0x00FF & stream.read()) << 24L) +
                    ((0x00FF & stream.read()) << 32L) + ((0x00FF & stream.read()) << 40L) +
                    ((0x00FF & stream.read()) << 48L) + ((0x00FF & stream.read()) << 56L);
        }
    }
    
    public void setBigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }
    
    public void skip(long numBytes) throws IOException {
        long bytesToSkip = numBytes;
        int numTries = 0;
        while(bytesToSkip != 0 && numTries++ < 0x10) // Convoluted statement, I know.
            bytesToSkip -= stream.skip(bytesToSkip);
    }

    public void close() throws IOException {
        stream.close();
    }
}
