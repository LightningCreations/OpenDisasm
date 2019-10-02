package com.lightning.opendisasm.disasm.clazz.node;

public class ClassNode extends AbstractEntityNode {
	
	public static final class InnerClassNode extends AbstractEntityNode{

		public InnerClassNode(String name, int modifiers, ClassNode parent) {
			super(name, modifiers, parent);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	private String superclass;
	
	public ClassNode(String name,int modifiers,String superclass) {
		super(name,modifiers,null);
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
