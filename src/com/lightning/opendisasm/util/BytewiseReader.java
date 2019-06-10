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
    
    public void setEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

    public void close() throws IOException {
        stream.close();
    }
}
