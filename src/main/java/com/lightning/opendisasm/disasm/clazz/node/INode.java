package com.lightning.opendisasm.disasm.clazz.node;

public interface INode {
	public String getName();
	public INode getParent();
	public INode[] getChildren();
	public int getModifiers();
}
