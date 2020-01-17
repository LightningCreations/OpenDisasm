package com.lightning.opendisasm.tree;

import com.lightning.opendisasm.util.OperandType;
import com.lightning.opendisasm.util.StringUtil;

public interface ValueNode extends Node {
	public OperandType getType();
	public Object getValue();
	
	public default String stringify(int numSpaces) {
        StringBuilder result = new StringBuilder();

        result.append(StringUtil.getSpaces(numSpaces)); result.append(getName()); result.append(": (");
        result.append(getType().getTypeName()); result.append(") ");
        if(getValue() instanceof Character) {
            result.append('\'');
            result.append(getValue());
            result.append('\'');
        } else if(getValue() instanceof String) {
            result.append('"');
            result.append(getValue());
            result.append('"');
        } else
            result.append(getValue());
        
        return result.toString();
    }
}
