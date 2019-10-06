package com.lightning.opendisasm.disasm.swf;

import java.io.IOException;
import java.io.InputStream;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;

public class SWFDisassembler extends Disassembler {
    public Node disassembleTree(InputStream file) {
        BytewiseReader r = new BytewiseReader(file);
        SWFFileNode head = new SWFFileNode(r);
        try {
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return head;
    }
}
