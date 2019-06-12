package com.lightning.opendisasm.disasm.clazz;

public class JVMOpcode {
	
	public enum Operand{
		Const, Opcode,
		Label, Local,
		WLocal, Byte,
		Short, Table;
	}
	
	
	private String opcodeName;
	private int opcodeId;
	private Operand[] ops;
	
	private static JVMOpcode[] opList = new JVMOpcode[256];
	
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
}
