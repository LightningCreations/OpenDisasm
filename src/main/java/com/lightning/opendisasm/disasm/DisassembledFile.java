package com.lightning.opendisasm.disasm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents a statically layed out form for storing a dissasembled file.
 * This has been superseeded by the Node layout of dissasembled files
 * @author chorm
 * @deprecated This has been superseeded as noted above by Node.
 */
public class DisassembledFile {
    private ArrayList<EnumNamer> namers;
    
    private HashMap<String, Object> header;
    private HashMap<String, String> headerTypes;
    private HashMap<Integer, String> headerOrdering;
    private int currentHeaderPos;
    private long entryPoint;
    
    public DisassembledFile() {
        namers = new ArrayList<>();
        
        header = new HashMap<>();
        headerTypes = new HashMap<>();
        headerOrdering = new HashMap<>();
    }
    
    public void addEnumNamer(EnumNamer namer) {
        if(!namers.contains(namer))
            namers.add(namer);
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
    
    public long getEntryPoint() {
        return entryPoint;
    }
    
    public void setEntryPoint(long entryPoint) {
        this.entryPoint = entryPoint;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        result.append("{\n");
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
            else if(value.getClass().isArray())
                result.append(Arrays.toString((Object[]) value));
            else
                result.append(value);
            if("enum".equals(type)) {
                String name = "unknown";
                for(int j = 0; j < namers.size() && "unknown".equals(name); j++)
                    name = namers.get(j).getEnumName(curField, value, this);
                result.append(" (");
                result.append(name);
                result.append(")");
            }
            result.append(";\n");
        }
        result.append("};");
        
        return result.toString();
    }
}
