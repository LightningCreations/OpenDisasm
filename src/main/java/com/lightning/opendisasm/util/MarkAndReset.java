package com.lightning.opendisasm.util;

import java.io.IOException;
import java.io.InputStream;

public final class MarkAndReset implements AutoCloseable {
	private InputStream strm;
	public MarkAndReset(InputStream strm,int limit) {
	    this.strm = strm;
		strm.mark(limit);
	}

	@Override
	public void close() throws IOException {
		strm.reset();
	}
}
