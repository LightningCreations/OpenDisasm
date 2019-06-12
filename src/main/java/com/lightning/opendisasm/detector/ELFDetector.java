package com.lightning.opendisasm.detector;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.lightning.opendisasm.detector.Detector.DetectorBase;
import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.ELFDisassembler;
import com.lightning.opendisasm.util.BytewiseReader;

public class ELFDetector extends DetectorBase {
    public boolean detect(byte[] file) {
        try (BytewiseReader reader = new BytewiseReader(new ByteArrayInputStream(file))) {
            if(reader.readMagic(4)!=0x7f454C46) {
                return false;
            }
            // More checks may be inserted later
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Class<? extends Disassembler> getDisasm() {
        return ELFDisassembler.class;
    }
}
