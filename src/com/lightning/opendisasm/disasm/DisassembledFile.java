package com.lightning.opendisasm.disasm;

import java.util.HashMap;

public class DisassembledFile {
    private HashMap<String, Object> header;
    private HashMap<String, String> headerTypes;
    private HashMap<Integer, String> headerOrdering;
    private int currentHeaderPos;
    
    public DisassembledFile() {
        header = new HashMap<>();
        headerTypes = new HashMap<>();
        headerOrdering = new HashMap<>();
    }
    
    public void addHeaderField(String type, String name, Object value) {
        if(!header.containsKey(name)) {
            headerOrdering.put(currentHeaderPos++, name);
        }
        header.put(name, value);
        headerTypes.put(name, type);
    }
    
    public String getHeaderFieldType(String field) {
        return headerTypes.get(field);
    }
    
    public Object getHeaderFieldValue(String field) {
        return header.get(field);
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        result.append("DisassembledFile result = {\n");
        for(int i = 0; i < headerOrdering.size(); i++) {
            String curField = headerOrdering.get(i);
            String type = headerTypes.get(curField);
            Object value = header.get(curField);
            result.append(type);
            result.append(' ');
            result.append(curField);
            result.append(" = ");
            if(value instanceof Number)
                result.append(String.format("0x%X", value));
            else
                result.append(value);
            result.append(";\n");
        }
        result.append("};");
        
        return result.toString();
    }
}
