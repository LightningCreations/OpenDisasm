package com.lightning.opendisasm.disasm.clazz.ref;

public class Handle {
	public static enum Kind{
		__ILLEGAL__,
		getField, putField, getStatic, putStatic,
		invokeVirtual, invokeStatic, invokeSpecial, newInvokeSpecial,
		invokeInterface;
	}
	private final Kind kind;
	private final ObjectRef ref;
	public Handle(Kind kind,ObjectRef ref) {
		this.kind = kind;
		this.ref = ref;
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public ObjectRef getRef() {
		return ref;
	}
	
	public String toString() {
		return kind+" "+ref;
	}
	

}
