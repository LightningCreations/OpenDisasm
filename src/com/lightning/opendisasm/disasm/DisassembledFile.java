package com.lightning.opendisasm.disasm;

import java.util.HashMap;

public class DisassembledFile {
    public HashMap<String, Object> header;
    
    public DisassembledFile() {
        header = new HashMap<>();
    }
    
    public void addHeaderField(String name, Object value) {
        header.put(name, value);
    }
}
