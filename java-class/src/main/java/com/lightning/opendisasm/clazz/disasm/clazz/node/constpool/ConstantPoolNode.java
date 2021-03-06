package com.lightning.opendisasm.clazz.disasm.clazz.node.constpool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lightning.opendisasm.clazz.disasm.clazz.node.ClassFile;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ConstantPoolNode implements Node {
	private ClassFile file;
	private List<ConstantNode> constants;
	
	public static <T> @NonNull T throwConstantError(String msg) {
		throw new RuntimeException(msg);
	}
	
	public ConstantPoolNode(BytewiseReader reader,ClassFile file) throws IOException {
		this.file = file;
		int len = reader.readUShort();
		constants = new ArrayList<>(len);
		for(;len>0;len--)
			constants.add(new ConstantNode(reader,this));
	}

	@Override
	public Node getParent() {
		// TODO Auto-generated method stub
		return file;
	}
	
	public ConstantNode getConstant(int idx,ConstantType type){
		ConstantNode tgt = constants.get(idx);
		if(tgt.getType().equals(type))
			return tgt;
		else
			return throwConstantError(String.format("Bad type for constant %d. Expected %s, got %s", idx,type,tgt.getType()));
	}

	@NonNull
	@Override
	public List<? extends @NonNull Node> getChildren() {
		// TODO Auto-generated method stub
		return constants;
	}

	@Override
	public String getName() {
		return "Class Constant Pool";
	}

}
