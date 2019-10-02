package com.lightning.opendisasm.disasm.clazz.node.attribute;

import com.lightning.opendisasm.disasm.clazz.node.AbstractEntityNode;
import com.lightning.opendisasm.disasm.clazz.node.EntityNode;

public abstract class AttributeNode extends AbstractEntityNode {

	
	private String attributeKind;
	
	protected AttributeNode(String attributeKind,String name, int modifiers, EntityNode parent) {
		super(name, modifiers, parent);
		// TODO Auto-generated constructor stub
	}
	
	public String getAttributeKind() {
		return attributeKind;
	}

}
