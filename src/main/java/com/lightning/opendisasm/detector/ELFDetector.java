package com.lightning.opendisasm.detector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.ELFDisassembler;
import com.lightning.opendisasm.util.BytewiseReader;

import javax.annotation.Nonnull;

public class ELFDetector extends DetectorBase {
    public boolean detect(InputStream file) {
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

    @Nonnull
    public Supplier<? extends Disassembler> getDisasm() {
        return ELFDisassembler::new;
    }
    
    public boolean handles(String target) {
    	return target.equals("elf");
    }
}
