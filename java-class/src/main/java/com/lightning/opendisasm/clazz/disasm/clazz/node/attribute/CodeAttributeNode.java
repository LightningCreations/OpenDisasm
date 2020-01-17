package com.lightning.opendisasm.clazz.disasm.clazz.node.attribute;

import com.lightning.opendisasm.clazz.disasm.clazz.node.EntityNode;
import com.lightning.opendisasm.util.BytewiseReader;

public class CodeAttributeNode extends AttributeNode {

	public CodeAttributeNode(String attributeKind, String name, EntityNode parent, BytewiseReader reader) {
		super(attributeKind, name, 0, parent,reader);
		// TODO Auto-generated constructor stub
	}

}
