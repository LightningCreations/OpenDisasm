package com.lightning.opendisasm.disasm.clazz;


public class JVMOpcode {
	
	
	public enum Operand{
		Const, WIns,
		Label, WLabel,
		Local, WLocal, 
		Byte , Short, 
		Table, Disc2,
		Disc1, Count;
	}
	
	
	private String opcodeName;
	private int opcodeId;
	private Operand[] ops;
	
	private static JVMOpcode[] opList = new JVMOpcode[256];
	private static WideForm[] wideforms = new WideForm[256];
	
	private static final class WideForm{
		final JVMOpcode op;
		final int id;
		final Operand[] ops;
		WideForm(JVMOpcode op,int id, Operand... ops) {
			assert wideforms[id]==null;
			this.op = op;
			this.id = id;
			this.ops = ops;
			wideforms[id] = this;
		}
	}
	
	private JVMOpcode(String name,int id, Operand... ops) {
		assert opList[id]==null;
		opList[id]=this;
		opcodeName = name;
		opcodeId = id;
		this.ops = ops;
	}
	
	public static final JVMOpcode aaload = new JVMOpcode("aaload",0x32);
	public static final JVMOpcode aastore = new JVMOpcode("aastore",0x53);
	public static final JVMOpcode aconst_null = new JVMOpcode("aconst_null",0x1);
	
	public static final JVMOpcode aload = new JVMOpcode("aload",0x19,Operand.Local);
	public static final JVMOpcode aload_0 = new JVMOpcode("aload_0",0x2a);
	public static final JVMOpcode aload_1 = new JVMOpcode("aload_1",0x2b);
	public static final JVMOpcode aload_2 = new JVMOpcode("aload_2",0x2c);
	public static final JVMOpcode aload_3 = new JVMOpcode("aload_3",0x2d);
	
	public static final JVMOpcode anewarray = new JVMOpcode("anewarray",0xbd,Operand.Const);
	public static final JVMOpcode areturn = new JVMOpcode("areturn",0xb0);
	public static final JVMOpcode arraylength = new JVMOpcode("arraylength",0xbe);
	
	public static final JVMOpcode astore = new JVMOpcode("astore",0x3a,Operand.Local);
	public static final JVMOpcode astore_0 = new JVMOpcode("astore_0",0x4b);
	public static final JVMOpcode astore_1 = new JVMOpcode("astore_1",0x4c);
	public static final JVMOpcode astore_2 = new JVMOpcode("astore_2",0x4d);
	public static final JVMOpcode astore_3 = new JVMOpcode("astore_3",0x4e);
	
	public static final JVMOpcode athrow = new JVMOpcode("athrow",0xbf);
	
	public static final JVMOpcode baload = new JVMOpcode("baload",0x33);
	public static final JVMOpcode bastore = new JVMOpcode("bastore",0x54);
	public static final JVMOpcode bipush = new JVMOpcode("bipush",0x10,Operand.Byte);
	
	public static final JVMOpcode caload = new JVMOpcode("caload",0x34);
	public static final JVMOpcode castore = new JVMOpcode("castore",0x55);
	public static final JVMOpcode checkcast = new JVMOpcode("checkcast",0xc0);
	
	public static final JVMOpcode d2f = new JVMOpcode("d2f",0x90);
	public static final JVMOpcode d2i = new JVMOpcode("d2i",0x8e);
	public static final JVMOpcode d2l = new JVMOpcode("d2l",0x8f);
	public static final JVMOpcode dadd = new JVMOpcode("dadd",0x63);
	public static final JVMOpcode daload = new JVMOpcode("daload",0x31);
	public static final JVMOpcode dastore = new JVMOpcode("dastore",0x52);
	public static final JVMOpcode dcmpg = new JVMOpcode("dcmpg",0x98);
	public static final JVMOpcode dcmpl = new JVMOpcode("dcmpl",0x97);
	public static final JVMOpcode dconst_0 = new JVMOpcode("dconst_0",0x0e);
	public static final JVMOpcode dconst_1 = new JVMOpcode("dconst_1",0x0f);
	public static final JVMOpcode ddiv = new JVMOpcode("ddiv",0x6f);
	
	public static final JVMOpcode dload = new JVMOpcode("dload",0x18);
	public static final JVMOpcode dload_0 = new JVMOpcode("dload_0",0x26);
	public static final JVMOpcode dload_1 = new JVMOpcode("dload_1",0x27);
	public static final JVMOpcode dload_2 = new JVMOpcode("dload_2",0x28);
	public static final JVMOpcode dload_3 = new JVMOpcode("dload_3",0x29);
	
	public static final JVMOpcode dmul = new JVMOpcode("dmul",0x6b);
	public static final JVMOpcode dneg = new JVMOpcode("dneg",0x77);
	public static final JVMOpcode drem = new JVMOpcode("drem",0x73);
	public static final JVMOpcode dreturn = new JVMOpcode("dreturn",0xaf);
	
	public static final JVMOpcode dstore = new JVMOpcode("dstore",0x39,Operand.Local);
	public static final JVMOpcode dstore_0 = new JVMOpcode("dstore_0",0x47);
	public static final JVMOpcode dstore_1 = new JVMOpcode("dstore_1",0x48);
	public static final JVMOpcode dstore_2 = new JVMOpcode("dstore_2",0x49);
	public static final JVMOpcode dstore_3 = new JVMOpcode("dstore_3",0x4a);
	
	public static final JVMOpcode dsub = new JVMOpcode("dsub",0x67);
	
	public static final JVMOpcode dup = new JVMOpcode("dup",0x59);
	public static final JVMOpcode dup_x1 = new JVMOpcode("dup_x1",0x5a);
	public static final JVMOpcode dup_x2 = new JVMOpcode("dup_x2",0x5b);
	public static final JVMOpcode dup2 = new JVMOpcode("dup2",0x5c);
	public static final JVMOpcode dup2_x1 = new JVMOpcode("dup2_x1",0x5d);
	public static final JVMOpcode dup2_x2 = new JVMOpcode("dup2_x2",0x5c);
	
	public static final JVMOpcode f2d = new JVMOpcode("f2d",0x8d);
	public static final JVMOpcode f2i = new JVMOpcode("f2i",0x8b);
	public static final JVMOpcode f2l = new JVMOpcode("f2l",0x8c);
	public static final JVMOpcode fadd = new JVMOpcode("fadd",0x62);
	public static final JVMOpcode faload = new JVMOpcode("faload",0x30);
	public static final JVMOpcode fastore = new JVMOpcode("fastore",0x51);
	public static final JVMOpcode fcmpg = new JVMOpcode("fcmpg",0x96);
	public static final JVMOpcode fcmpl = new JVMOpcode("fcmpl",0x97);
	public static final JVMOpcode fconst_0 = new JVMOpcode("fconst_0",0xb);
	public static final JVMOpcode fconst_1 = new JVMOpcode("fconst_1",0xc);
	public static final JVMOpcode fconst_2 = new JVMOpcode("fconst_2",0xd);
	public static final JVMOpcode fdiv = new JVMOpcode("fdiv",0x6e);
	
	public static final JVMOpcode fload = new JVMOpcode("fload",0x17,Operand.Local);
	public static final JVMOpcode fload_0 = new JVMOpcode("fload_0",0x22);
	public static final JVMOpcode fload_1 = new JVMOpcode("fload_1",0x23);
	public static final JVMOpcode fload_2 = new JVMOpcode("fload_2",0x24);
	public static final JVMOpcode fload_3 = new JVMOpcode("fload_3",0x25);
	
	public static final JVMOpcode fmul = new JVMOpcode("fmul",0x6a);
	public static final JVMOpcode fneg = new JVMOpcode("fneg",0x76);
	public static final JVMOpcode frem = new JVMOpcode("frem",0x72);
	public static final JVMOpcode freturn = new JVMOpcode("freturn",0xae);
	
	public static final JVMOpcode fstore = new JVMOpcode("fstore",0x38,Operand.Local);
	public static final JVMOpcode fstore_0 = new JVMOpcode("fstore_0",0x43);
	public static final JVMOpcode fstore_1 = new JVMOpcode("fstore_1",0x44);
	public static final JVMOpcode fstore_2 = new JVMOpcode("fstore_2",0x45);
	public static final JVMOpcode fstore_3 = new JVMOpcode("fstore_3",0x46);
	
	public static final JVMOpcode fsub = new JVMOpcode("fsub",0x66);
	
	public static final JVMOpcode getfield = new JVMOpcode("getfield",0xb4,Operand.Const);
	public static final JVMOpcode getstatic = new JVMOpcode("getstatic",0xb2,Operand.Const);
	public static final JVMOpcode goto_ins = new JVMOpcode("goto",0xa7,Operand.Label);
	public static final JVMOpcode goto_w = new JVMOpcode("goto_w",0xc8,Operand.WLabel);
	
	public static final JVMOpcode i2b = new JVMOpcode("i2b",0x91);
	public static final JVMOpcode i2c = new JVMOpcode("i2c",0x92);
	public static final JVMOpcode i2d = new JVMOpcode("i2d",0x87);
	public static final JVMOpcode i2f = new JVMOpcode("i2f",0x86);
	public static final JVMOpcode i2l = new JVMOpcode("i2l",0x85);
	public static final JVMOpcode i2s = new JVMOpcode("i2s",0x93);
	public static final JVMOpcode iadd = new JVMOpcode("iadd",0x60);
	public static final JVMOpcode iaload = new JVMOpcode("iaload",0x2e);
	public static final JVMOpcode iand = new JVMOpcode("iand",0x7e);
	public static final JVMOpcode iastore = new JVMOpcode("iastore",0x4f);
	
	public static final JVMOpcode iconst_m1 = new JVMOpcode("iconst_m1",0x2);
	public static final JVMOpcode iconst_0 = new JVMOpcode("iconst_0",0x3);
	public static final JVMOpcode iconst_1 = new JVMOpcode("iconst_1",0x4);
	public static final JVMOpcode iconst_2 = new JVMOpcode("iconst_2",0x5);
	public static final JVMOpcode iconst_3 = new JVMOpcode("iconst_3",0x6);
	public static final JVMOpcode iconst_4 = new JVMOpcode("iconst_4",0x7);
	public static final JVMOpcode iconst_5 = new JVMOpcode("iconst_5",0x8);
	
	public static final JVMOpcode idiv = new JVMOpcode("idiv",0x6c);
	
	public static final JVMOpcode if_acmpeq = new JVMOpcode("if_acmpeq",0xa5,Operand.Label);
	public static final JVMOpcode if_acmpne = new JVMOpcode("if_acmpne",0xa6,Operand.Label);
	public static final JVMOpcode if_icmpeq = new JVMOpcode("if_icmpeq",0x9f,Operand.Label);
	public static final JVMOpcode if_icmpne = new JVMOpcode("if_icmpne",0xa0,Operand.Label);
	public static final JVMOpcode if_icmplt = new JVMOpcode("if_icmplt",0xa1,Operand.Label);
	public static final JVMOpcode if_icmpge = new JVMOpcode("if_icmpge",0xa2,Operand.Label);
	public static final JVMOpcode if_icmpgt = new JVMOpcode("if_icmpgt",0xa3,Operand.Label);
	public static final JVMOpcode if_icmple = new JVMOpcode("if_icmple",0xa4,Operand.Label);
	public static final JVMOpcode ifeq = new JVMOpcode("ifeq",0x99,Operand.Label);
	public static final JVMOpcode ifne = new JVMOpcode("ifne",0x9a,Operand.Label);
	public static final JVMOpcode iflt = new JVMOpcode("iflt",0x9b,Operand.Label);
	public static final JVMOpcode ifge = new JVMOpcode("ifge",0x9c,Operand.Label);
	public static final JVMOpcode ifgt = new JVMOpcode("ifgt",0x9d,Operand.Label);
	public static final JVMOpcode ifle = new JVMOpcode("ifle",0x9e,Operand.Label);
	public static final JVMOpcode ifnonnull = new JVMOpcode("ifnonnul",0xc7,Operand.Label);
	public static final JVMOpcode ifnull = new JVMOpcode("ifnull",0xc6);
	
	public static final JVMOpcode iinc = new JVMOpcode("iinc",0x84,Operand.Local,Operand.Byte);
	
	public static final JVMOpcode iload = new JVMOpcode("iload",0x15,Operand.Local);
	public static final JVMOpcode iload_0 = new JVMOpcode("iload_0",0x1a);
	public static final JVMOpcode iload_1 = new JVMOpcode("iload_1",0x1b);
	public static final JVMOpcode iload_2 = new JVMOpcode("iload_2",0x1c);
	public static final JVMOpcode iload_3 = new JVMOpcode("iload_3",0x1d);
	
	public static final JVMOpcode imul = new JVMOpcode("imul",0x68);
	public static final JVMOpcode ineg = new JVMOpcode("ineg",0x74);
	
	public static final JVMOpcode instanceof_ins = new JVMOpcode("instanceof",0xc1);
	
	public static final JVMOpcode invokedynamic = new JVMOpcode("invokedynamic",0xba,Operand.Const,Operand.Disc2);
	public static final JVMOpcode invokeinterface = new JVMOpcode("invokeinterface",0xb9,Operand.Const,Operand.Count,Operand.Disc1);
	public static final JVMOpcode invokespecial = new JVMOpcode("invokespecial",0xb7,Operand.Const);
	public static final JVMOpcode invokestatic = new JVMOpcode("invokestatic",0xb8,Operand.Const);
	public static final JVMOpcode invokevirtual = new JVMOpcode("invokevirtual",0xb6,Operand.Const);
	
	public static final JVMOpcode ior = new JVMOpcode("ior",0x80);
	public static final JVMOpcode irem = new JVMOpcode("irem",0x70);
	public static final JVMOpcode ireturn = new JVMOpcode("ireturn",0xac);
	public static final JVMOpcode ishl = new JVMOpcode("ishl",0x78);
	public static final JVMOpcode ishr = new JVMOpcode("ishr",0x7a);
	
	public static final JVMOpcode istore = new JVMOpcode("istore",0x36);
	public static final JVMOpcode istore_0 = new JVMOpcode("istore_0",0x3b);
	public static final JVMOpcode istore_1 = new JVMOpcode("istore_1",0x3c);
	public static final JVMOpcode istore_2 = new JVMOpcode("istore_2",0x3d);
	public static final JVMOpcode istore_3 = new JVMOpcode("istore_3",0x3e);
	
	public String toString() {
		return Integer.toHexString(opcodeId)+" "+opcodeName;
	}
}
