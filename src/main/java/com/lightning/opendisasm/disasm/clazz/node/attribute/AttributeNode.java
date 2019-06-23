package com.lightning.opendisasm.disasm.clazz.node.attribute;

import com.lightning.opendisasm.disasm.clazz.node.AbstractNode;
import com.lightning.opendisasm.disasm.clazz.node.INode;

public abstract class AttributeNode extends AbstractNode {

	
	private String attributeKind;
	
	protected AttributeNode(String attributeKind,String name, int modifiers, INode parent) {
		super(name, modifiers, parent);
		// TODO Auto-generated constructor stub
	}
	
	public String getAttributeKind() {
		return attributeKind;
	}

}
