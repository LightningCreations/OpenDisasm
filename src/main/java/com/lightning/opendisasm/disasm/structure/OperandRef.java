package com.lightning.opendisasm.disasm.structure;

import java.util.HashMap;

public class OperandRef {
    private static HashMap<Operand, Operand> prevOps;
    public Operand op;
    
    public OperandRef(Operand op) {
        if(!prevOps.containsKey(op)) prevOps.put(op, op);
        this.op = prevOps.get(op);
        this.op.refs.add(this);
    }
}
