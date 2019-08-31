package com.lightning.opendisasm.disasm.clazz.node.constpool;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;

public class ConstantNode implements Node {
	
	private ConstantPoolNode parent;
	private Object value;
	private ConstantType tag;
	
	public ConstantNode(ConstantType tag,Object value,ConstantPoolNode parent) {
		this.tag = tag;
		this.value = value;
		this.parent = parent;
	}
	
	public ConstantNode(BytewiseReader reader,ConstantPoolNode node) throws IOException {
		int tag = reader.readUByte();
		this.tag = ConstantType.getTypeForTag(tag);
	}

	@Override
	public Node getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}
	public ConstantType getType(){
		return tag;
	}
	
	public Object getValue() {
		return value;
	}

}
