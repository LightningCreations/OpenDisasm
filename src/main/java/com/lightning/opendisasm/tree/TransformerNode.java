package com.lightning.opendisasm.tree;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.lightning.opendisasm.detector.Detector;

public class TransformerNode implements Node {
	private final Node parent;
	private Node child;
	private final String target;
	private final Consumer<TransformerNode> andThen;
	
	public TransformerNode(Node parent,Node child,String target) {
		this(parent,child,target,t->{});
	}
	public TransformerNode(Node parent,Node child,String target,Consumer<TransformerNode> andThen) {
		this.parent = parent;
		this.child = child;
		this.target = target;
		this.andThen = andThen;
	}
	
	@Override
	public Node getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Node getChild() {
		return child;
	}
	
	public void replaceChild(Node node) {
		this.child = node;
	}
	
	public void transform() {
		Detector.getTransformerFor(target).get().applyTo(this);
		this.andThen.accept(this);
	}

	@Override
	public List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Arrays.asList(child);
	}

}
