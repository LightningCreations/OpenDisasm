package com.lightning.opendisasm.detector;

import com.lightning.opendisasm.disasm.Disassembler;

public class Detector {
    public static abstract class DetectorBase {
        public abstract boolean detect(byte[] file);
        
        public abstract Class<? extends Disassembler> getDisasm();
    }
    
    @SuppressWarnings("rawtypes")
    private static Class[] detectors = {ELFDetector.class};
    
    public static Class<? extends Disassembler> getDisasmFor(byte[] file) {
        for(int i = 0; i < detectors.length; i++) {
            try {
                DetectorBase detector = ((Class<DetectorBase>) detectors[i]).newInstance();
                if(detector.detect(file)) return detector.getDisasm();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                System.out.println("Well, this is embarrassing. " + detectors[i].getSimpleName() + " doesn't have a public no-input contructor!");
                continue;
            }
        }
        return null;
    }
}
