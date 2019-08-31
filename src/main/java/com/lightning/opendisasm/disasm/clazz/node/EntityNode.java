package com.lightning.opendisasm.disasm.clazz.node;

import com.lightning.opendisasm.tree.Node;

public interface EntityNode extends Node {
	public String getName();
	public int getModifiers();
}
