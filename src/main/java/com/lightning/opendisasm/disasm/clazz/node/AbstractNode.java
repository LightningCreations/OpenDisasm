package com.lightning.opendisasm.disasm.clazz.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lightning.opendisasm.tree.Node;

public abstract class AbstractNode implements EntityNode {
	
	private final String name;
	private final int modifiers;
	private final EntityNode parent;
	private final List<EntityNode> children;
	
	protected void addChild(EntityNode child) {
		assert child.getParent()==this;
		children.add(child);
	}
	
	public AbstractNode(String name, int modifiers, EntityNode parent) {
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
	public EntityNode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public int getModifiers() {
		// TODO Auto-generated method stub
		return modifiers;
	}

	@Override
	public List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableList(children);
	}

}
