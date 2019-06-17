package com.lightning.opendisasm.disasm.clazz.node;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode implements INode {
	
	private final String name;
	private final int modifiers;
	private final INode parent;
	private final List<INode> children;
	
	protected void addChild(INode child) {
		assert child.getParent()==this;
		children.add(child);
	}
	
	public AbstractNode(String name, int modifiers, INode parent) {
		this.name = name;
		this.modifiers = modifiers;
		this.parent = parent;
		this.children = new ArrayList<>();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public INode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public int getModifiers() {
		// TODO Auto-generated method stub
		return modifiers;
	}

	@Override
	public INode[] getChildren() {
		// TODO Auto-generated method stub
		return children.toArray(new INode[0]);
	}

}
