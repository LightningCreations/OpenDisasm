package com.lightning.opendisasm.detector;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.ELFDisassembler;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;


public class ELFDetector extends DetectorBase {
    public boolean detect(@NonNull InputStream file) {
        try (BytewiseReader reader = new BytewiseReader(file)) {
            if(reader.readMagicInt(4)!=0x7f454C46) {
                return false;
            }
            // More checks may be inserted later
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @NonNull
    public Supplier<? extends Disassembler> getDisasm() {
        return ELFDisassembler::new;
    }
    
    public boolean handles(@NonNull String target) {
    	return target.equals("elf");
    }
}
