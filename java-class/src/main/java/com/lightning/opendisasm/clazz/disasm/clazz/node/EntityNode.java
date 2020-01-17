package com.lightning.opendisasm.clazz.disasm.clazz.node;

import com.lightning.opendisasm.tree.Node;

public interface EntityNode extends Node {
	public String getName();
	public int getModifiers();
}
