package com.lightning.opendisasm.tests;

import com.lightning.opendisasm.detector.Detector;
import com.lightning.opendisasm.tree.Node;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassDefinition;

public class TestClassDisasm {


    public void testClassFile(ClassDefinition defn){
        byte[] data = defn.getDefinitionClassFile();
        ByteArrayInputStream strm = new ByteArrayInputStream(data);
        Node node = Detector.diassembleTreeFromStream(strm);
    }
}
