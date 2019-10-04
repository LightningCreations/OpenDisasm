package com.lightning.opendisasm.disasm.clazz;

import java.io.InputStream;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.clazz.node.ClassFile;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;

public class ClassFileDisassembler extends Disassembler {

	public ClassFileDisassembler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Node disassembleTree(InputStream file) {
		// TODO Auto-generated method stub
		return new ClassFile(new BytewiseReader(file));
	}

}
