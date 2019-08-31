package com.lightning.opendisasm.detector;

import java.io.IOException;
import java.io.InputStream;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.util.MarkAndReset;

public class Detector {
    public static abstract class DetectorBase {
        public abstract boolean detect(InputStream file);
        
        public abstract Class<? extends Disassembler> getDisasm();
    }
    
    @SuppressWarnings("rawtypes")
    private static Class[] detectors = {ELFDetector.class,ClassFileDetector.class};
    
    public static Class<? extends Disassembler> getDisasmFor(InputStream file) {
        for(int i = 0; i < detectors.length; i++) {
            try(MarkAndReset raii = new MarkAndReset(file,1024)) {
                DetectorBase detector = (DetectorBase) detectors[i].newInstance();
                if(detector.detect(file)) return detector.getDisasm();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                System.out.println("Well, this is embarrassing. " + detectors[i].getSimpleName() + " doesn't have a public no-input contructor!");
                continue;
            } catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        return null;
    }
}
