package com.lightning.opendisasm.disasm.clazz.node;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.lightning.opendisasm.disasm.clazz.JVMVersion;
import com.lightning.opendisasm.disasm.clazz.node.constpool.ConstantPoolNode;
import com.lightning.opendisasm.tree.MetadataNode;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;

import javax.annotation.Nonnull;

public class ClassFile implements Node {
	public static void throwParserError(String msg) {
		throw new RuntimeException(msg);
	}
	public static final class FileMagicAndVersionNode implements MetadataNode{
		private int major;
		private int minor;
		private ClassFile file;
		
		
		
		public FileMagicAndVersionNode(ClassFile file,BytewiseReader reader) throws IOException {
			if(reader.readMagicInt(4)!=0xCAFEBABE)
				throwParserError("Bad Magic");
			minor = reader.readUShort();
			major = reader.readUShort();
			this.file = file;
		}
		
		public int getMajorVersion() {
			return major;
		}
		public int getMinorVersion() {
			return minor;
		}
		
		@Override
		public Node getParent() {
			// TODO Auto-generated method stub
			return file;
		}

		@Nonnull
		@Override
		public List<? extends Node> getChildren() {
			// TODO Auto-generated method stub
			return Collections.emptyList();
		}

		@Override
		public String getName() {
			return JVMVersion.getVersionFromCode(major);
		}

		@Override
		public String getTagName() {
			// TODO Auto-generated method stub
			return "Java Version";
		}
		
	}
	private ConstantPoolNode pool;
	private ClassNode node;
	public ClassFile(BytewiseReader reader) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Node getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Nonnull
	@Override
	public List<? extends Node> getChildren() {
		// TODO Auto-generated method stub
		return Arrays.asList(pool,node);
	}

	@Override
	public String getName() {
		return "(disassembled class file)";
	}

}
