package com.lightning.opendisasm.detector;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import com.lightning.opendisasm.disasm.DisassembledFile;
import com.lightning.opendisasm.disasm.Disassembler;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.MarkAndReset;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Detector {
    public @Nullable
	static DisassembledFile disassembleStream(InputStream file) {
    	if(!file.markSupported())
    		return disassembleStream(new BufferedInputStream(file));
    	 return Optional.ofNullable(getDisasmFor(file)).map(Supplier::get).map(d->d.disassemble(file)).orElse(null);
    }
    
    public @Nullable static Node disassembleTreeFromStream(InputStream file) {
    	if(!file.markSupported())
    		return disassembleTreeFromStream(new BufferedInputStream(file));
    	return Optional.ofNullable(getDisasmFor(file)).map(Supplier::get).map(d->d.disassembleTree(file)).orElse(null);
    }
    
    private static class UnbreakMarkAndResetInputStream extends FilterInputStream{

		UnbreakMarkAndResetInputStream(InputStream in) {
			super(in);
			// TODO Auto-generated constructor stub
		}

		@Override
		public synchronized void mark(int readlimit) {
			
		}

		@Override
		public synchronized void reset() throws IOException {
			throw new IOException("Marks are not supported within detect");
		}

		@Override
		public boolean markSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		public void close(){}
    	
    }
    
    public static Supplier<? extends Disassembler> getDisasmFor(InputStream file) {
    	if(!file.markSupported())
    		throw new RuntimeException("Cannot detect file type, file does not support marks");
        for(DetectorBase detector:ServiceLoader.load(DetectorBase.class)) {
            try(MarkAndReset ignored = new MarkAndReset(file,1024)) {
            	try(UnbreakMarkAndResetInputStream unbreak = new UnbreakMarkAndResetInputStream(file)){
            		if(detector.detect(unbreak))return detector.getDisasm();
            	}
            } catch (IOException e1) {
				throw new RuntimeException(e1);
			}
        }
        return null;
    }
    
    public static Optional<Supplier<? extends @NonNull Disassembler>> getTransformerFor(String target){
    	for(DetectorBase detector:ServiceLoader.load(DetectorBase.class))
    		if(detector.handles(target))
    			return Optional.of(detector.getDisasm());
    	return Optional.empty();
    }
}
