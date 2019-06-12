package com.lightning.opendisasm.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class BytewiseReader implements Closeable {
    private InputStream stream;
    private boolean bigEndian;
    
    public BytewiseReader(InputStream stream) {
        this.stream = stream;
    }
    
    public String readMagic(int length) throws IOException {
        StringBuilder resultBuilder = new StringBuilder();
        for(int i = 0; i < length && stream.available() > 0; i++) {
            resultBuilder.append((char) (0x00FF & stream.read()));
        }
        return resultBuilder.toString();
    }
    
    public short readUByte() throws IOException {
        return (short) (0x00FF & stream.read());
    }
    
    public int readUShort() throws IOException {
        if(bigEndian) {
            return ((0x00FF & stream.read()) << 8) + (0x00FF & stream.read());
        } else {
            return (0x00FF & stream.read()) + ((0x00FF & stream.read()) << 8);
        }
    }
    
    public long readUInt() throws IOException {
        if(bigEndian) {
            return  ((0x00FF & stream.read()) << 24) + ((0x00FF & stream.read()) << 16) +
                    ((0x00FF & stream.read()) <<  8) + ((0x00FF & stream.read())      );
        } else {
            return  ((0x00FF & stream.read())      ) + ((0x00FF & stream.read()) <<  8) +
                    ((0x00FF & stream.read()) << 16) + ((0x00FF & stream.read()) << 24);
        }
    }
    
    public long readULong() throws IOException {
        if(bigEndian) {
            return  ((0x00FF & stream.read()) << 56) + ((0x00FF & stream.read()) << 48) +
                    ((0x00FF & stream.read()) << 40) + ((0x00FF & stream.read()) << 32) +
                    ((0x00FF & stream.read()) << 24) + ((0x00FF & stream.read()) << 16) +
                    ((0x00FF & stream.read()) <<  8) + ((0x00FF & stream.read())      );
        } else {
            return  ((0x00FF & stream.read())      ) + ((0x00FF & stream.read()) << 8) +
                    ((0x00FF & stream.read()) << 16) + ((0x00FF & stream.read()) << 24) +
                    ((0x00FF & stream.read()) << 32) + ((0x00FF & stream.read()) << 40) +
                    ((0x00FF & stream.read()) << 48) + ((0x00FF & stream.read()) << 56);
        }
    }
    
    public void setEndian(boolean bigEndian) {
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
