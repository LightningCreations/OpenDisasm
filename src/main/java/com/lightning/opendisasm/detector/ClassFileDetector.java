package com.lightning.opendisasm.detector;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.detector.Detector.DetectorBase;
import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.clazz.ClassFileDisassembler;

public class ClassFileDetector extends DetectorBase {
	
	private static final int CAFEBABE = 0xCAFEBABE;
	
	public ClassFileDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean detect(InputStream file) {
		try (DataInputStream reader = new DataInputStream(file)){
			if(reader.readInt()==CAFEBABE)
				return true;
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public Supplier<? extends Disassembler> getDisasm() {
		// TODO Auto-generated method stub
		return ClassFileDisassembler::new;
	}

}
