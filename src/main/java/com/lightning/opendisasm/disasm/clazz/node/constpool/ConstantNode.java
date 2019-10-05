package com.lightning.opendisasm.disasm.clazz.node.constpool;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;


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

	@NonNull
	@Override
	public List<? extends @NonNull Node> getChildren() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return tag.toString();
	}

	public ConstantType getType(){
		return tag;
	}
	
	public Object getValue() {
		return value;
	}

}
