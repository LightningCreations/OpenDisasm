package com.lightning.opendisasm.tree;

import java.util.List;

public interface Node {
	public Node getParent();
	public List<? extends Node> getChildren();
}
