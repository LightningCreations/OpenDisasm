package com.lightning.opendisasm.tree;

import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public interface Node {
	/**
	 * Returns the parent of this node.
	 * A Node may have no parent, represented by this method returning null.
	 */
	public @Nullable Node getParent();

	/**
	 * Gets all the Children of this node, or an empty list.
	 * For all Children c of this node, c.getParent().equals(this) shall be true.
	 */
	public @Nonnull List<? extends Node> getChildren();

	public String getName();
}
