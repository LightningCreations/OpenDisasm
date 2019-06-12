package com.lightning.opendisasm.disasm;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.lightning.opendisasm.util.BytewiseReader;

public class ELFDisassembler extends Disassembler {
    public DisassembledFile disassemble(byte[] file) {
        DisassembledFile result = new DisassembledFile();
        try(BytewiseReader reader = new BytewiseReader(new ByteArrayInputStream(file))) {
            reader.skip(4); // e_ident[EI_MAG0], e_ident[EI_MAG1], e_ident[EI_MAG2], e_ident[EI_MAG3]
            short EI_CLASS = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_CLASS]", EI_CLASS);
            boolean longInt = EI_CLASS == 2;
            short EI_DATA = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_DATA]", EI_DATA);
            reader.setEndian(EI_DATA == 2);
            short EI_VERSION = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_VERSION]", EI_VERSION);
            short EI_OSABI = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_OSABI]", EI_OSABI);
            short EI_ABIVERSION = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_ABIVERSION]", EI_ABIVERSION);
            reader.skip(7); // EI_PAD
            int e_type = reader.readUShort();
            result.addHeaderField("enum", "e_type", e_type);
            int e_machine = reader.readUShort();
            result.addHeaderField("enum", "e_machine", e_machine);
            long e_version = reader.readUInt();
            result.addHeaderField("enum", "e_version", e_version);
            long e_entry, e_phoff, e_shoff;
            if(longInt) {
                result.addHeaderField("uint64", "e_entry", e_entry = reader.readULong());
                result.addHeaderField("uint64", "e_phoff", e_phoff = reader.readULong());
                result.addHeaderField("uint64", "e_shoff", e_shoff = reader.readULong());
            } else {
                result.addHeaderField("uint32", "e_entry", e_entry = reader.readUInt());
                result.addHeaderField("uint32", "e_phoff", e_phoff = reader.readUInt());
                result.addHeaderField("uint32", "e_shoff", e_shoff = reader.readUInt());
            }
            long e_flags = reader.readUInt();
            result.addHeaderField("uint32", "e_flags", e_flags);
            int e_ehsize = reader.readUShort();
            result.addHeaderField("uint16", "e_ehsize", e_ehsize);
            int e_phentsize = reader.readUShort();
            result.addHeaderField("uint16", "e_phentsize", e_phentsize);
            int e_phnum = reader.readUShort();
            result.addHeaderField("uint16", "e_phnum", e_phnum);
            int e_shentsize = reader.readUShort();
            result.addHeaderField("uint16", "e_shentsize", e_shentsize);
            int e_shnum = reader.readUShort();
            result.addHeaderField("uint16", "e_shnum", e_shnum);
            int e_shstrndx = reader.readUShort();
            result.addHeaderField("uint16", "e_shstrndx", e_shstrndx);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }
}
