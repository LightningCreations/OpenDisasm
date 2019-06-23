package com.lightning.opendisasm.disasm.clazz.ref;

public class ClassRef {
	
	private String cl;
	
	public ClassRef(String cl) {
		this.cl = cl;
	}
	
	public String toString() {
		return cl+".class";
	}

}
