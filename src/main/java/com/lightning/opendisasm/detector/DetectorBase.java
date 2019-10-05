package com.lightning.opendisasm.detector;

import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.Disassembler;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class DetectorBase {
    public abstract boolean detect(InputStream file);
    
    public abstract boolean handles(String targetName);
    
    public @NonNull abstract Supplier<? extends @NonNull Disassembler> getDisasm();
}