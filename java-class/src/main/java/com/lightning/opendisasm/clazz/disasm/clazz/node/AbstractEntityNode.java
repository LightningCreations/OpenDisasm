package com.lightning.opendisasm.clazz.disasm.clazz.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;


public abstract class AbstractEntityNode implements EntityNode {
	private final String name;
	private final int modifiers;
	private final Node parent;
	private final List<EntityNode> children;
	
	protected final void addChild(EntityNode child) {
		assert child.getParent()==this;
		children.add(child);
	}
	
	public AbstractEntityNode(String name, int modifiers, Node parent, BytewiseReader reader) {
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
	public Node getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public int getModifiers() {
		// TODO Auto-generated method stub
		return modifiers;
	}

	@NonNull
	@Override
	public List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableList(children);
	}
}
