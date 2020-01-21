package com.lightning.opendisasm.selftest.start;

import com.lightning.opendisasm.detector.Detector;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.ValueNode;
import com.lightning.opendisasm.util.StringUtil;
import com.lightning.opendisasm.visit.NodeVisitor;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;

public class Main {
    private static class ClassPrintVisitor implements NodeVisitor{
        private final int tabs;

        ClassPrintVisitor(PrintStream stream, int tabs){
            this.output = stream;
            this.tabs = tabs;
        }
        private PrintStream output;
        @Override
        public Optional<NodeVisitor> visitChild(Node child) {
            if(child instanceof ValueNode)
                return Optional.of(new ClassPrintVisitor(output,1));
            output.println();
            return Optional.of(new ClassPrintVisitor(output, tabs + 1));
        }
        public void visitTree(Node n){
            output.print(StringUtil.getSpaces(tabs*4)+n.stringify());
            NodeVisitor.super.visitTree(n);
        }
    }
    private static void runSelfTest(InputStream stream){
        Node n = Detector.disassembleTreeFromStream(stream);
        new ClassPrintVisitor(System.out,0).visitTree(n);
    }
    public static void main(String[] args){
        Arrays.stream(args).map(Main.class::getResourceAsStream).forEach(Main::runSelfTest);
    }
}
