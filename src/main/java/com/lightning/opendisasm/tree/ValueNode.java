package com.lightning.opendisasm.tree;

import com.lightning.opendisasm.util.OperandType;

public interface ValueNode extends Node {
	public OperandType getType();
}
