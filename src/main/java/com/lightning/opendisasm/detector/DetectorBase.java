package com.lightning.opendisasm.detector;

import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.Disassembler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class DetectorBase {
    public abstract boolean detect(InputStream file);
    
    public abstract boolean handles(String targetName);
    
    public @Nonnull abstract Supplier<? extends Disassembler> getDisasm();
}