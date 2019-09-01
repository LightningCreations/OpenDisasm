package com.lightning.opendisasm.detector;

import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.Disassembler;

public abstract class DetectorBase {
    public abstract boolean detect(InputStream file);
    
    public abstract Supplier<? extends Disassembler> getDisasm();
}