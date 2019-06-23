package com.lightning.opendisasm.disasm.clazz;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import com.lightning.opendisasm.util.BytewiseReader;

public abstract class Constant {
	
	private static final List<BiFunction<ConstantPool,BytewiseReader,? extends Constant>>
			 constantGenerators = Collections.unmodifiableList(Arrays.asList(
					 null,
					 ConstUTF8::new,
					 null
					 ));
	
	protected final ConstantPool pool;
	static class ConstUTF8 extends Constant{
		private String utf8String;
		ConstUTF8(ConstantPool pool,byte[] chars){
			super(pool,0);
			utf8String = new String(chars,StandardCharsets.UTF_8);
		}
		ConstUTF8(ConstantPool pool,BytewiseReader reader){
			super(pool,0);
			try {
				utf8String = reader.readString(StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		@Override
		public String getType() {
			// TODO Auto-generated method stub
			return "utf8";
		}

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return utf8String;
		}
		
	}
	
	static class ConstInt extends Constant{
		ConstInt(ConstantPool pool,int value) {
			super(pool, 3);
			this.value = value;
		}
		ConstInt(ConstantPool pool,BytewiseReader reader){
			super(pool,3);
			this.value = reader.readInt();
		}

		private int value;

		@Override
		public String getType() {
			// TODO Auto-generated method stub
			return "integer";
		}

		@Override
		public Integer getValue() {
			// TODO Auto-generated method stub
			return value;
		}
	}
	
	private int tag;
	
	protected Constant(ConstantPool pool,int tag) {
		this.pool = pool;
		this.tag = tag;
	}
	
	public int getTag() {
		return tag;
	}
	public abstract String getType();
	public abstract Object getValue();
	public String toString() {
		return getType()+": "+getValue();
	}
	

}
