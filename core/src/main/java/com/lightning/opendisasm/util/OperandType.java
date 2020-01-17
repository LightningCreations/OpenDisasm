package com.lightning.opendisasm.util;

import com.lightning.opendisasm.tree.Node;

public interface OperandType {
	public String getTypeName();
	public String parseNodeAsType(Node node);
}
