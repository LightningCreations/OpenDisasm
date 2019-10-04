package com.lightning.opendisasm.disasm;

import java.io.IOException;
import java.io.InputStream;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;

public class ELFDisassembler extends Disassembler implements EnumNamer {
    public class ProgramHeaderEntry { // Why is this non-static again?
        public long p_type; // All fields are long to reduce chance of integer overflow
        public long p_flags;
        public long p_offset;
        public long p_vaddr;
        public long p_paddr;
        public long p_filesz;
        public long p_memsz;
        public long p_align;
        public boolean longInt;
        
        public String toString() {
            StringBuilder result = new StringBuilder();
            
            result.append("{\n");

            result.append("enum p_type = ");
            result.append(String.format("0x%X", p_type));
            result.append(" (");
            switch((int) p_type) {
            case 1:
                result.append("PT_LOAD");
                break;
            case 2:
                result.append("PT_DYNAMIC");
                break;
            case 3:
                result.append("PT_INTERP");
                break;
            case 4:
                result.append("PT_NOTE");
                break;
            case 6:
                result.append("PT_PHDR");
                break;
            case 0x6474E550:
                result.append("PT_GNU_EH_FRAME");
                break;
            case 0x6474E551:
                result.append("PT_GNU_STACK");
                break;
            case 0x6474E552:
                result.append("PT_GNU_RELRO");
                break;
            default:
                result.append("unknown");
                break;
            }
            result.append(");\n");
            
            if(longInt) {
                result.append("uint32_t p_flags = ");
                result.append(String.format("0x%X", p_flags));
                result.append(";\n");
                
                result.append("Elf64_Off p_offset = ");
                result.append(String.format("0x%X", p_offset));
                result.append(";\n");
                
                result.append("Elf64_Addr p_vaddr = ");
                result.append(String.format("0x%X", p_vaddr));
                result.append(";\n");
                
                result.append("Elf64_Addr p_paddr = ");
                result.append(String.format("0x%X", p_paddr));
                result.append(";\n");
                
                result.append("uint64_t p_filesz = ");
                result.append(String.format("0x%X", p_filesz));
                result.append(";\n");
                
                result.append("uint64_t p_memsz = ");
                result.append(String.format("0x%X", p_memsz));
                result.append(";\n");
                
                result.append("uint64_t p_align = ");
                result.append(String.format("0x%X", p_align));
                result.append(";\n");
            } else {
                result.append("Elf32_Off p_offset = ");
                result.append(String.format("0x%X", p_offset));
                result.append(";\n");
                
                result.append("Elf32_Addr p_vaddr = ");
                result.append(String.format("0x%X", p_vaddr));
                result.append(";\n");
                
                result.append("Elf32_Addr p_paddr = ");
                result.append(String.format("0x%X", p_paddr));
                result.append(";\n");
                
                result.append("uint32_t p_filesz = ");
                result.append(String.format("0x%X", p_filesz));
                result.append(";\n");
                
                result.append("uint32_t p_memsz = ");
                result.append(String.format("0x%X", p_memsz));
                result.append(";\n");
                
                result.append("uint32_t p_flags = ");
                result.append(String.format("0x%X", p_flags));
                result.append(";\n");
                
                result.append("uint32_t p_align = ");
                result.append(String.format("0x%X", p_align));
                result.append(";\n");
            }
            
            result.append("}");
            
            return result.toString();
        }
    }
    
    public class SectionHeaderEntry { //Same Here
        public long sh_name; // All fields are long to reduce chance of integer overflow
        public long sh_type;
        public long sh_flags;
        public long sh_addr;
        public long sh_offset;
        public long sh_size;
        public long sh_link;
        public long sh_info;
        public long sh_addralign;
        public long sh_entsize;
        public boolean longInt;
        
        public String toString() {
            StringBuilder result = new StringBuilder();
            
            result.append("{\n");
            
            result.append("uint32_t sh_name = ");
            result.append(String.format("0x%X", sh_name));
            result.append(";\n");
            
            result.append("enum sh_type = ");
            result.append(String.format("0x%X", sh_type));
            result.append(" (");
            switch((int) sh_type) {
            case 0:
                result.append("SHT_NULL");
                break;
            case 1:
                result.append("SHT_PROGBITS");
                break;
            case 3:
                result.append("SHT_STRTAB");
                break;
            case 4:
                result.append("SHT_RELA");
                break;
            case 6:
                result.append("SHT_DYNAMIC");
                break;
            case 7:
                result.append("SHT_NOTE");
                break;
            case 8:
                result.append("SHT_NOBITS");
                break;
            case 0xB:
                result.append("SHT_DYNSYM");
                break;
            case 0xE:
                result.append("SHT_INIT_ARRAY");
                break;
            case 0xF:
                result.append("SHT_FINI_ARRAY");
                break;
            case 0x6FFFFFF6:
                result.append("SHT_GNU_HASH");
                break;
            case 0x6FFFFFFE:
                result.append("SHT_GNU_verneed");
                break;
            case 0x6FFFFFFF:
                result.append("SHT_GNU_versym");
                break;
            default:
                result.append("unknown");
                break;
            }
            result.append(");\n");
            
            if(longInt) {
                result.append("uint64_t sh_flags = ");
                result.append(String.format("0x%X", sh_flags));
                result.append(" (");
                long tmpFlags = sh_flags;
                boolean alreadyFlagged = false;
                if((tmpFlags & 0x1) != 0) {
                    if(alreadyFlagged) result.append(" | ");
                    else alreadyFlagged = true;
                    result.append("SHF_WRITE");
                    tmpFlags -= 0x1;
                }
                if((tmpFlags & 0x2) != 0) {
                    if(alreadyFlagged) result.append(" | ");
                    else alreadyFlagged = true;
                    result.append("SHF_ALLOC");
                    tmpFlags -= 0x2;
                }
                if((tmpFlags & 0x4) != 0) {
                    if(alreadyFlagged) result.append(" | ");
                    else alreadyFlagged = true;
                    result.append("SHF_EXECINSTR");
                    tmpFlags -= 0x4;
                }
                if((tmpFlags & 0x10) != 0) {
                    if(alreadyFlagged) result.append(" | ");
                    else alreadyFlagged = true;
                    result.append("SHF_MERGE");
                    tmpFlags -= 0x10;
                }
                if((tmpFlags & 0x20) != 0) {
                    if(alreadyFlagged) result.append(" | ");
                    else alreadyFlagged = true;
                    result.append("SHF_STRINGS");
                    tmpFlags -= 0x20;
                }
                if((tmpFlags & 0x40) != 0) {
                    if(alreadyFlagged) result.append(" | ");
                    else alreadyFlagged = true;
                    result.append("SHF_INFO_LINK");
                    tmpFlags -= 0x40;
                }
                if(tmpFlags != 0) {
                    if(alreadyFlagged) result.append(" | ");
                    else alreadyFlagged = true;
                    result.append(String.format("%X", tmpFlags));
                }
                if(!alreadyFlagged) result.append("SHF_NONE");
                result.append(");\n");

                result.append("Elf64_Addr sh_addr = ");
                result.append(String.format("0x%X", sh_addr));
                result.append(";\n");

                result.append("Elf64_Off sh_offset = ");
                result.append(String.format("0x%X", sh_offset));
                result.append(";\n");

                result.append("uint64_t sh_size = ");
                result.append(String.format("0x%X", sh_size));
                result.append(";\n");
            } else {
                result.append("uint32_t sh_flags = ");
                result.append(String.format("0x%X", sh_flags));
                result.append(";\n");

                result.append("Elf32_Addr sh_addr = ");
                result.append(String.format("0x%X", sh_addr));
                result.append(";\n");

                result.append("Elf32_Off sh_offset = ");
                result.append(String.format("0x%X", sh_offset));
                result.append(";\n");

                result.append("uint32_t sh_size = ");
                result.append(String.format("0x%X", sh_size));
                result.append(";\n");
            }

            result.append("uint32_t sh_link = ");
            result.append(String.format("0x%X", sh_link));
            result.append(";\n");

            result.append("uint32_t sh_info = ");
            result.append(String.format("0x%X", sh_info));
            result.append(";\n");
            
            if(longInt) {
                result.append("uint64_t sh_addralign = ");
                result.append(String.format("0x%X", sh_addralign));
                result.append(";\n");

                result.append("uint64_t sh_entsize = ");
                result.append(String.format("0x%X", sh_entsize));
                result.append(";\n");
            } else {
                result.append("uint32_t sh_addralign = ");
                result.append(String.format("0x%X", sh_addralign));
                result.append(";\n");

                result.append("uint32_t sh_entsize = ");
                result.append(String.format("0x%X", sh_entsize));
                result.append(";\n");
            }
            
            result.append("}");
            
            return result.toString();
        }
    }
    
    public DisassembledFile disassemble(InputStream file) {
        DisassembledFile result = new DisassembledFile();
        result.addEnumNamer(this);
        try(BytewiseReader reader = new BytewiseReader(file)) {
            reader.skip(4); // e_ident[EI_MAG0], e_ident[EI_MAG1], e_ident[EI_MAG2], e_ident[EI_MAG3]
            short EI_CLASS = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_CLASS]", EI_CLASS);
            boolean longInt = EI_CLASS == 2;
            short EI_DATA = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_DATA]", EI_DATA);
            reader.setBigEndian(EI_DATA == 2);
            short EI_VERSION = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_VERSION]", EI_VERSION);
            short EI_OSABI = reader.readUByte();
            result.addHeaderField("enum", "e_ident[EI_OSABI]", EI_OSABI);
            short EI_ABIVERSION = reader.readUByte();
            result.addHeaderField("uint8_t", "e_ident[EI_ABIVERSION]", EI_ABIVERSION);
            reader.skip(7); // EI_PAD
            int e_type = reader.readUShort();
            result.addHeaderField("enum", "e_type", e_type);
            int e_machine = reader.readUShort();
            result.addHeaderField("enum", "e_machine", e_machine);
            long e_version = reader.readUInt();
            result.addHeaderField("enum", "e_version", e_version);
            long e_entry, e_phoff, e_shoff;
            if(longInt) {
                result.addHeaderField("Elf64_Addr", "e_entry", e_entry = reader.readULong());
                result.addHeaderField("Elf64_Off", "e_phoff", e_phoff = reader.readULong());
                result.addHeaderField("Elf64_Off", "e_shoff", e_shoff = reader.readULong());
            } else {
                result.addHeaderField("Elf32_Addr", "e_entry", e_entry = reader.readUInt());
                result.addHeaderField("Elf32_Off", "e_phoff", e_phoff = reader.readUInt());
                result.addHeaderField("Elf32_Off", "e_shoff", e_shoff = reader.readUInt());
            }
            result.setEntryPoint(e_entry);
            long e_flags = reader.readUInt();
            result.addHeaderField("uint32_t", "e_flags", e_flags);
            int e_ehsize = reader.readUShort();
            result.addHeaderField("uint16_t", "e_ehsize", e_ehsize);
            int e_phentsize = reader.readUShort();
            result.addHeaderField("uint16_t", "e_phentsize", e_phentsize);
            int e_phnum = reader.readUShort();
            result.addHeaderField("uint16_t", "e_phnum", e_phnum);
            int e_shentsize = reader.readUShort();
            result.addHeaderField("uint16_t", "e_shentsize", e_shentsize);
            int e_shnum = reader.readUShort();
            result.addHeaderField("uint16_t", "e_shnum", e_shnum);
            int e_shstrndx = reader.readUShort();
            result.addHeaderField("uint16_t", "e_shstrndx", e_shstrndx);
            
            reader.skipTo(e_phoff);
            ProgramHeaderEntry[] ph = new ProgramHeaderEntry[e_phnum];
            for(int i = 0; i < e_phnum; i++) {
                ph[i] = new ProgramHeaderEntry();
                ph[i].longInt = longInt;
                ph[i].p_type = reader.readUInt();
                if(longInt) {
                    ph[i].p_flags = reader.readUInt();
                    ph[i].p_offset = reader.readULong();
                    ph[i].p_vaddr = reader.readULong();
                    ph[i].p_paddr = reader.readULong();
                    ph[i].p_filesz = reader.readULong();
                    ph[i].p_memsz = reader.readULong();
                    ph[i].p_align = reader.readULong();
                } else {
                    ph[i].p_offset = reader.readUInt();
                    ph[i].p_vaddr = reader.readUInt();
                    ph[i].p_paddr = reader.readUInt();
                    ph[i].p_filesz = reader.readUInt();
                    ph[i].p_memsz = reader.readUInt();
                    ph[i].p_flags = reader.readUInt();
                    ph[i].p_align = reader.readUInt();
                }
            }
            if(longInt)
                result.addHeaderField("Elf64_Phdr[e_phnum]", "ph", ph);
            else
                result.addHeaderField("Elf32_Phdr[e_phnum]", "ph", ph);
            
            reader.skipTo(e_shoff);
            SectionHeaderEntry[] sh = new SectionHeaderEntry[e_shnum];
            for(int i = 0; i < e_shnum; i++) {
                sh[i] = new SectionHeaderEntry();
                sh[i].longInt = longInt;
                sh[i].sh_name = reader.readUInt();
                sh[i].sh_type = reader.readUInt();
                if(longInt) {
                    sh[i].sh_flags = reader.readULong();
                    sh[i].sh_addr = reader.readULong();
                    sh[i].sh_offset = reader.readULong();
                    sh[i].sh_size = reader.readULong();
                } else {
                    sh[i].sh_flags = reader.readUInt();
                    sh[i].sh_addr = reader.readUInt();
                    sh[i].sh_offset = reader.readUInt();
                    sh[i].sh_size = reader.readUInt();
                }
                sh[i].sh_link = reader.readUInt();
                sh[i].sh_info = reader.readUInt();
                if(longInt) {
                    sh[i].sh_addralign = reader.readULong();
                    sh[i].sh_entsize = reader.readULong();
                } else {
                    sh[i].sh_addralign = reader.readUInt();
                    sh[i].sh_entsize = reader.readUInt();
                }
            }
            if(longInt)
                result.addHeaderField("Elf64_Shdr[e_phnum]", "sh", sh);
            else
                result.addHeaderField("Elf32_Shdr[e_phnum]", "sh", sh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public String getEnumName(String varName, Object _value, DisassembledFile reault) {
        int value = ((Number)_value).intValue();
        
        if("e_ident[EI_CLASS]".equals(varName)) {
            switch(value) {
            case 0:
                return "ELFCLASSNONE";
            case 1:
                return "ELFCLASS32";
            case 2:
                return "ELFCLASS64";
            }
        } else if("e_ident[EI_DATA]".equals(varName)) {
            switch(value) {
            case 0:
                return "ELFDATANONE";
            case 1:
                return "ELFDATA2LSB";
            case 2:
                return "ELFDATA2MSB";
            }
        } else if("e_ident[EI_VERSION]".equals(varName)) {
            switch(value) {
            case 0:
                return "EV_NONE";
            case 1:
                return "EV_CURRENT";
            }
        } else if("e_ident[EI_OSABI]".equals(varName)) {
            switch(value) {
            case 0:
                return "ELFOSABI_NONE";
            case 3:
                return "ELFOSABI_LINUX";
            }
        } else if("e_type".equals(varName)) {
            switch(value) {
            case 0:
                return "ET_NONE";
            case 3:
                return "ET_DYN";
            }
        } else if("e_machine".equals(varName)) {
            switch(value) {
            case 0:
                return "EM_NONE";
            case 0x3E:
                return "EM_X86_64";
            }
        } else if("e_version".equals(varName)) {
            switch(value) {
            case 0:
                return "EV_NONE";
            case 1:
                return "EV_CURRENT";
            }
        }
        
        return "unknown";
    }

	@Override
	public Node disassembleTree(InputStream file) {
		// TODO Auto-generated method stub
		return null;
	}
}
