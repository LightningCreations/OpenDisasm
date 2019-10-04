package com.lightning.opendisasm.disasm.clazz.node;

import com.lightning.opendisasm.util.BytewiseReader;

public class ClassNode extends AbstractEntityNode {
	
	public static final class InnerClassNode extends AbstractEntityNode{

		public InnerClassNode(String name, int modifiers, ClassNode parent) {
			super(name, modifiers, parent,null);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	private String superclass;
	
	public ClassNode(String name, int modifiers, ClassFile file, String superclass, BytewiseReader reader) {
		super(name,modifiers,file,reader);
		this.superclass = superclass;
	}
	
	public String getSuperclass() {
		return superclass;
	}
	
	public InnerClassNode addInnerClass(String name,int modifiers) {
		InnerClassNode inner = new InnerClassNode(getName()+"$"+name,modifiers,this);
		this.addChild(inner);
		return inner;
	}
}
