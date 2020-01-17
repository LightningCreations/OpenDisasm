package com.lightning.opendisasm.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;

public final class MarkAndReset implements AutoCloseable {
	private InputStream strm;
	public MarkAndReset(@NonNull InputStream strm, int limit) {
	    this.strm = strm;
		strm.mark(limit);
	}

	@Override
	public void close() throws IOException {
		strm.reset();
	}
}
