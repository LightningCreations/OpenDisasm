package com.lightning.opendisasm.tree;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;



import javax.annotation.Nonnull;

public final class OctetStreamNode implements Node {
	private final byte[] data;
	private final Node parent;
	
	public OctetStreamNode(@Nonnull byte[] data,@Nonnull Node parent) {
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

	@Nonnull
	@Override
	public List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return "(raw bytes)";
	}

}
