package com.lightning.opendisasm.tree;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;




public final class OctetStreamNode implements Node {
	private final byte[] data;
	private final Node parent;
	
	public OctetStreamNode(@NonNull byte[] data, @NonNull Node parent) {
		this.data = data;
		this.parent = parent;
	}
	
	@Override
	public Node getParent() {
		// TODO Auto-generated method stub
		return parent;
	}
	 
	public InputStream newInputStream() {
		return new ByteArrayInputStream(data);
	}

	@NonNull
	@Override
	public List<? extends @NonNull Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return "(raw bytes)";
	}

}
