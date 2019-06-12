package com.lightning.opendisasm.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightning.opendisasm.detector.Detector;
import com.lightning.opendisasm.disasm.DisassembledFile;
import com.lightning.opendisasm.disasm.Disassembler;

public class CLIMain {
    private static final Logger LOGGER = LogManager.getLogger(CLIMain.class);
    public static void main(String[] args) {
        LOGGER.info("Starting...");
        
        LOGGER.trace("Creating Options...");
        Options options = new Options();
        options.addOption("i", "interactive", false, "interactive prompt (WIP)");
        LOGGER.trace("Done creating Options");

        LOGGER.trace("Creating CommandLineParser...");
        CommandLineParser parser = new DefaultParser();
        LOGGER.trace("Parsing command line...");
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args, true);
        } catch (ParseException e) {
            LOGGER.fatal(e.getLocalizedMessage());
            System.exit(1);
        }
        if(cmd.hasOption('i')) {
            LOGGER.fatal("Interactive mode not yet supported!");
            System.exit(1);
        }
        if(cmd.getArgList().isEmpty()) {
            LOGGER.fatal("No file specified to disassemble!");
            System.exit(1);
        }
        LOGGER.trace("Done with command line parsing.");

        LOGGER.trace("Grabbing file into byte[] for easier manipulation...");
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(new File(cmd.getArgList().get(0)));
        } catch (FileNotFoundException e) {
            LOGGER.fatal("Couldn't load file: " + e.getLocalizedMessage());
            System.exit(1);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputFile, output);
        } catch (IOException e) {
            LOGGER.fatal("Couldn't load file: " + e.getLocalizedMessage());
            System.exit(1);
        }
        byte[] file = output.toByteArray();
        LOGGER.trace("Done converting file.");

        LOGGER.trace("Determining disassembler...");
        Class<? extends Disassembler> disassemblerClass = Detector.getDisasmFor(file);
        LOGGER.debug("Disassembler determined as " + disassemblerClass.getName());

        LOGGER.trace("Instantiating disassembler...");
        Disassembler disassembler = null;
        try {
            disassembler = disassemblerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.fatal("Some idiot (most likely me) messed up the constructors...");
            System.exit(1);
        }
        LOGGER.trace("Disassembler created.");
        
        LOGGER.info("Disassembling...");
        DisassembledFile result = disassembler.disassemble(file);
        LOGGER.trace("Done disassembling");
        LOGGER.info("Result:\n" + result.toString());
    }
}
