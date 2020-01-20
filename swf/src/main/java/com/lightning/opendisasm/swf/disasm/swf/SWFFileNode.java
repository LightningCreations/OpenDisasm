package com.lightning.opendisasm.swf.disasm.swf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.OctetStreamNode;
import com.lightning.opendisasm.tree.TransformerNode;
import com.lightning.opendisasm.tree.ValueNode;
import com.lightning.opendisasm.util.BytewiseReader;
import com.lightning.opendisasm.util.OperandType;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


public class SWFFileNode implements Node {
    private static final ArrayList<Node> emptyList = new ArrayList<>();
    
    public ArrayList<Node> children = new ArrayList<>();
    
    private ValueNode constructNode(String name, String type, Object value) {
        return new ValueNode() {
            public @Nullable Node getParent() {
                return SWFFileNode.this;
            }

            public @NonNull List<? extends @NonNull Node> getChildren() {
                return emptyList;
            }

            public @NonNull String getName() {
                return name;
            }

            public OperandType getType() {
                return new OperandType() {
                    public String getTypeName() {
                        return type;
                    }

                    public String parseNodeAsType(Node node) {
                        return null;
                    }
                };
            }

            public Object getValue() {
                return value;
            }
        };
    }
    
    private static void disassemblePt2(TransformerNode transf) {
        
    }
    
    public SWFFileNode(BytewiseReader r) {
        try {
            short sig0;
            children.add(constructNode("Signature[0]", "uint8_t", (char)(sig0 = r.readUByte())));
            children.add(constructNode("Signature[1]", "uint8_t", (char)r.readUByte()));
            children.add(constructNode("Signature[2]", "uint8_t", (char)r.readUByte()));
            children.add(constructNode("Version", "uint8_t", (Short)r.readUByte()));
            children.add(constructNode("FileLength", "uint32_t", (Long)r.readUInt()));
            Function<? super Node, ? extends @NonNull Node> octetStreamGen = (Node parent) -> {
                try {
                    return new OctetStreamNode(r.readRemaining(), parent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            
            if(sig0 == 0x46) { // uncompressed
                children.add(new TransformerNode(this, octetStreamGen, "thru", (TransformerNode transf) -> {
                    disassemblePt2(transf);
                }));
            } else if(sig0 == 0x43) { // ZLib
                children.add(new TransformerNode(this, octetStreamGen, "zlib", (TransformerNode transf) -> {
                    disassemblePt2(transf);
                }));
            } else if(sig0 == 0x5A) { // LZMA
                children.add(new TransformerNode(this, octetStreamGen, "lzma", (TransformerNode transf) -> {
                    disassemblePt2(transf);
                }));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public Node getParent() {
        return null;
    }

    @NonNull
    public List<? extends @NonNull Node> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return "(disassembled SWF file)";
    }
}
