package com.lightning.opendisasm.clazz.disasm.clazz.ref;

public class ObjectRef {
	private final ClassRef cl;
	private final NameAndType nameAndType;
	public ObjectRef(ClassRef cl,NameAndType nameAndType) {
		this.cl = cl;
		this.nameAndType = nameAndType;
	}
	
	public ClassRef getOwningClass() {
		return cl;
	}
	
	public NameAndType getNameAndType() {
		return nameAndType;
	}
	
	public String toString() {
		return cl+"."+nameAndType;
	}

}
