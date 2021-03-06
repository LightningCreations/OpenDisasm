package com.lightning.opendisasm.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.lightning.opendisasm.detector.Detector;
import org.checkerframework.checker.nullness.qual.NonNull;



public class TransformerNode implements Node {
	private final Node parent;
	private Node child;
	private final String target;
	private final Consumer<TransformerNode> andThen;
	
	public TransformerNode(Node parent, Function<? super TransformerNode,? extends @NonNull Node> child, String target) {
		this(parent,child,target,t->{});
	}
	public TransformerNode(Node parent, Function<? super TransformerNode, ? extends @NonNull Node> child, String target, Consumer<TransformerNode> andThen) {
		this.parent = Objects.requireNonNull(parent);
		this.target = Objects.requireNonNull(target);
		this.andThen = Objects.requireNonNull(andThen);
		this.child = Objects.requireNonNull(child).apply(this);
	}
	
	@Override
	public Node getParent() {
		// TODO Auto-generated method stub
		return parent;
	}
	
	public Node getChild() {
		return child;
	}
	
	public void replaceChild(@NonNull Node node) {
		this.child = Objects.requireNonNull(node);
	}
	
	
	public void transform() {
		Detector.getTransformerFor(target).ifPresent(s->s.get().applyTo(this));
		this.andThen.accept(this);
	}

	@Override
	public @NonNull List<? extends @NonNull Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.singletonList(child);
	}

	@Override
	public String getName() {
		return "(transformer)";
	}
}
