package com.lightning.opendisasm.disasm.clazz;

import java.nio.charset.StandardCharsets;

public abstract class Constant {
	protected final ConstantPool pool;
	class ConstUTF8 extends Constant{
		private String utf8String;
		ConstUTF8(ConstantPool pool,byte[] chars){
			super(pool);
			utf8String = new String(chars,StandardCharsets.UTF_8);
		}
		@Override
		public String getType() {
			// TODO Auto-generated method stub
			return "utf8";
		}

		@Override
		public Object getValue() {
			// TODO Auto-generated method stub
			return utf8String;
		}
		
	}
	
	protected Constant(ConstantPool pool) {
		this.pool = pool;
	}
	
	public abstract String getType();
	public abstract Object getValue();
	public String toString() {
		return getType()+": "+getValue();
	}
	

}
