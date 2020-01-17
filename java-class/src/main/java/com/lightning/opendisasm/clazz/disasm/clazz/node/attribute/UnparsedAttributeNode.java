package com.lightning.opendisasm.clazz.disasm.clazz.node.attribute;

import com.lightning.opendisasm.clazz.disasm.clazz.node.EntityNode;
import com.lightning.opendisasm.util.BytewiseReader;

import java.io.IOException;

public class UnparsedAttributeNode extends AttributeNode {
    private byte[] data;
    protected UnparsedAttributeNode(String attributeKind, String name, int modifiers, EntityNode parent, BytewiseReader reader) throws IOException {
        super(attributeKind, name, modifiers, parent, reader);
        int len = reader.readUShort();
        data = reader.readBytes(len);
    }

    public byte[] getData(){
        return data.clone();
    }
}
