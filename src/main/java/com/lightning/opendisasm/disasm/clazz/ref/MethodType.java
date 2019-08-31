package com.lightning.opendisasm.disasm.clazz.ref;

public class MethodType {
	private String descriptor;
	public MethodType(String descriptor) {
		this.descriptor = descriptor;
	}
	
	public String toString() {
		return descriptor;
	}

}
