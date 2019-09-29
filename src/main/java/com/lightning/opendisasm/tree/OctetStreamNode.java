package com.lightning.opendisasm.tree;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.lightning.opendisasm.util.BytewiseReader;

public final class OctetStreamNode implements Node {
	private byte[] data;
	private final Node parent;
	
	public OctetStreamNode(byte[] data,Node parent) {
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

	@Override
	public List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

}
