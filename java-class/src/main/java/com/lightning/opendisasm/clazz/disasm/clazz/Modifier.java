package com.lightning.opendisasm.clazz.disasm.clazz;

public class Modifier {
	public enum ModifierDomain{
		Class, Field,
		Method, Local,
		InnerClass;
	}
	
	private int value;
	private ModifierDomain domain;
	
	public static interface Class{
		Modifier ACC_PUBLIC = new Modifier(0x0001,ModifierDomain.Class);
		Modifier ACC_FINAL = new Modifier(0x0010,ModifierDomain.Class);
		
	}
	
	private Modifier(int mask,ModifierDomain domain) {
		this.value = mask;
		this.domain = domain;
	}
	
	public int getMask() {
		return value;
	}
}
