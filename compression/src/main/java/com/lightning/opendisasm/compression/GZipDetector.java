package com.lightning.opendisasm.compression;

import com.lightning.opendisasm.detector.DetectorBase;
import com.lightning.opendisasm.disasm.Disassembler;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class GZipDetector extends DetectorBase {
    @Override
    public boolean detect(InputStream file) {
        try {
            if (file.read() != 0x1f)
                return false;
            else if (file.read() != 0x8b)
                return false;
            return true;
        }catch(IOException e){
            return false;
        }
    }

    @Override
    public boolean handles(String targetName) {
        return targetName.equalsIgnoreCase("gzip");
    }

    @Override
    public @NonNull Supplier<? extends Disassembler> getDisasm() {
        return null;
    }
}
