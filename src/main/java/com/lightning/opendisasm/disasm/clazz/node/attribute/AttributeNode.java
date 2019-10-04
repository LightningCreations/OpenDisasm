package com.lightning.opendisasm.disasm.clazz.node.attribute;

import com.lightning.opendisasm.disasm.clazz.node.AbstractEntityNode;
import com.lightning.opendisasm.disasm.clazz.node.EntityNode;
import com.lightning.opendisasm.util.BytewiseReader;

public abstract class AttributeNode extends AbstractEntityNode {
    private String attributeKind;
	
	protected AttributeNode(String attributeKind, String name, int modifiers, EntityNode parent, BytewiseReader reader) {
		super(name, modifiers, parent,reader);
		// TODO Auto-generated constructor stub
	}
	
	public String getAttributeKind() {
		return attributeKind;
	}
}
