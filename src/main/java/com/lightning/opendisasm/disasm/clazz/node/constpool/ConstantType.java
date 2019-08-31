package com.lightning.opendisasm.disasm.clazz.node.constpool;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.lightning.opendisasm.disasm.clazz.ModifiedUtf8Charset;
import com.lightning.opendisasm.disasm.clazz.node.ClassFile;
import com.lightning.opendisasm.util.BytewiseReader;
import com.lightning.opendisasm.util.BytewiseReader.StringType;

public abstract class ConstantType {
	private final int tag;
	private final String readName;
	private final int minJvmVersion;
	
	
	public String toString() {
		return readName;
	}
	
	public static final ConstantType CONST_Utf8 = new ConstantTypeUtf8();
	
	public static final class ConstantTypeUtf8 extends ConstantType{

		private ConstantTypeUtf8() {
			super(1, "utf8",45);
			// TODO Auto-generated constructor stub
		}

		@Override
		public String parse(BytewiseReader reader, ConstantPoolNode constants) throws IOException {
			// TODO Auto-generated method stub
			return reader.readString(ModifiedUtf8Charset.modified, StringType.LengthPrefixed);
		}
		
	}
	
	public static final class ConstantTypeInteger extends ConstantType{
		private ConstantTypeInteger() {
			super(3,"int",45);
		}

		@Override
		public Integer parse(BytewiseReader reader, ConstantPoolNode constants) throws IOException {
			// TODO Auto-generated method stub
			return reader.readInt();
		}
	}
	
	private static List<? extends ConstantType> types = Arrays.asList(null,CONST_Utf8);
	
	public abstract Object parse(BytewiseReader reader,ConstantPoolNode constants)throws IOException;
	
	public int entriesRequired() {
		return 1;
	}
	
	ConstantType(int tag,String readName,int minJvmVersion) {
		this.tag = tag;
		this.readName = readName;
		this.minJvmVersion = minJvmVersion;
	}
	
	public int getTag() {
		return tag;
	}

	public static ConstantType getTypeForTag(int tag) {
		// TODO Auto-generated method stub
		return Objects.requireNonNull(types.get(tag),"Bad Constant Tag"+tag);
	}
	
	public void checkValid(int jvmMajorVersion) {
		if(minJvmVersion>jvmMajorVersion)
			ClassFile.throwParserError(String.format("Constants of type %s cannot appear in class files indicating major version %d (minimum version %d)", this,jvmMajorVersion,minJvmVersion));
	}
}
