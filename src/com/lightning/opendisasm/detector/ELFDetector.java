package com.lightning.opendisasm.detector;

import java.io.ByteArrayInputStream;

import com.lightning.opendisasm.detector.Detector.DetectorBase;
import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.util.BytewiseReader;

public class ELFDetector extends DetectorBase {
    public boolean detect(byte[] file) {
        BytewiseReader reader = new BytewiseReader(new ByteArrayInputStream(file));
        if(file[0] != 0x7F || file[1] != 'E' || file[2] != 'L' || file[3] != 'F') {
            return false;
        }
        // More checks may be inserted later
        return true;
    }

    public Class<? extends Disassembler> getDisasm() {
        // TODO Auto-generated method stub
        return null;
    }
}
