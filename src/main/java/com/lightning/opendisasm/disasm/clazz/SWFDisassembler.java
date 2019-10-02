package com.lightning.opendisasm.disasm.clazz;

import java.io.InputStream;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.swf.SWFFileNode;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;

public class SWFDisassembler extends Disassembler {
    public Node disassembleTree(InputStream file) {
        BytewiseReader r = new BytewiseReader(file);
        SWFFileNode head = new SWFFileNode(r);
        return head;
    }
}
