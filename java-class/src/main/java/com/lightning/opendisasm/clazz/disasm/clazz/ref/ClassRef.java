package com.lightning.opendisasm.clazz.disasm.clazz.ref;

public class ClassRef {
	
	private final String cl;
	
	public ClassRef(String cl) {
		this.cl = cl;
	}
	
	public String toString() {
		return cl+".class";
	}

}
