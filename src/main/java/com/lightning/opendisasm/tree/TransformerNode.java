package com.lightning.opendisasm.tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.lightning.opendisasm.detector.Detector;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TransformerNode implements Node {
	private final Node parent;
	private Node child;
	private final String target;
	private final Consumer<TransformerNode> andThen;
	
	public TransformerNode(Node parent,Node child,String target) {
		this(parent,child,target,t->{});
	}
	public TransformerNode(Node parent,Node child,String target,Consumer<TransformerNode> andThen) {
		this.parent = Objects.requireNonNull(parent);
		this.child = Objects.requireNonNull(child);
		this.target = Objects.requireNonNull(target);
		this.andThen = Objects.requireNonNull(andThen);
	}
	
	@Override
	public Node getParent() {
		// TODO Auto-generated method stub
		return parent;
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
	public @Nonnull List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.singletonList(child);
	}

	@Override
	public String getName() {
		return "(transformer)";
	}

}
