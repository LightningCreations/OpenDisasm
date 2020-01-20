package com.lightning.opendisasm.compression.gzip;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;

public class GZipDisassembler extends Disassembler {
    @Override
    public @NonNull Node disassembleTree(@NonNull InputStream file) {
        try {
            try(BytewiseReader reader = new BytewiseReader(file)){

            }
        } catch (IOException e) {
        }
        return null;
    }
}
