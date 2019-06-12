package com.lightning.opendisasm.detector;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.lightning.opendisasm.detector.Detector.DetectorBase;
import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.util.BytewiseReader;

public class ClassFileDetector extends DetectorBase {
	
	private static final int CAFEBABE = 0xCAFEBABE;
	
	public ClassFileDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean detect(byte[] file) {
		try (DataInputStream reader = new DataInputStream(new ByteArrayInputStream(file))){
			if(reader.readInt()==CAFEBABE)
				return true;
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public Class<? extends Disassembler> getDisasm() {
		// TODO Auto-generated method stub
		return null;
	}

}
