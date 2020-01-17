package com.lightning.opendisasm.disasm;

public interface EnumNamer {
    public String getEnumName(String varName, Object value, DisassembledFile result);
}
