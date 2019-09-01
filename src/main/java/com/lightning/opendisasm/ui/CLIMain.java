package com.lightning.opendisasm.ui;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
    public static void main(String[] args) throws IOException {
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
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(new File(cmd.getArgList().get(0)));
        } catch (FileNotFoundException e) {
            LOGGER.fatal("Couldn't load file: " + e.getLocalizedMessage());
            System.exit(1);
        }
        
        DisassembledFile result = Detector.diassembleStream(inputFile);
        if(result==null) {
        	LOGGER.error("No detector available that understands the input file");
        	System.exit(1);
        }
        LOGGER.trace("Done disassembling");
        LOGGER.info("Result:\n" + result.toString());
        BufferedWriter printer = new BufferedWriter(new FileWriter("result.txt"));
        printer.write(result.toString());
        printer.close();
    }
}
