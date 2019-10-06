package com.lightning.opendisasm.detector;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.swf.SWFDisassembler;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;


public class SWFDetector extends DetectorBase {
    @Override
    public boolean detect(@NonNull InputStream file) {
        try (BytewiseReader reader = new BytewiseReader(file)) {
            char sig1 = (char)reader.readUByte();
            if(sig1 != 'F' && sig1 != 'C' && sig1 != 'Z') return false;
            char sig2 = (char)reader.readUByte();
            if(sig2 != 'W') return false;
            char sig3 = (char)reader.readUByte();
            if(sig3 != 'S') return false;
            // More checks may be inserted later
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean handles(@NonNull String targetName) {
        return targetName.equals("swf");
    }

    @NonNull
    @Override
    public Supplier<? extends @NonNull Disassembler> getDisasm() {
        return SWFDisassembler::new;
    }
}
