package com.lightning.opendisasm.detector;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.disasm.clazz.ClassFileDisassembler;
import org.checkerframework.checker.nullness.qual.NonNull;


public class ClassFileDetector extends DetectorBase {
	
	private static final int CAFEBABE = 0xCAFEBABE;
	
	public ClassFileDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean detect(@NonNull InputStream file) {
		try (DataInputStream reader = new DataInputStream(file)){
			if(reader.readInt()==CAFEBABE)
				return true;
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	@NonNull
	@Override
	public Supplier<? extends Disassembler> getDisasm() {
		// TODO Auto-generated method stub
		return ClassFileDisassembler::new;
	}

	@Override
	public boolean handles(@NonNull String targetName) {
		// TODO Auto-generated method stub
		return targetName.equalsIgnoreCase("class");
	}

}
