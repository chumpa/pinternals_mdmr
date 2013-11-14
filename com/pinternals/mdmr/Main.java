package com.pinternals.mdmr;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class Main {
	private static Logger log = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		InputStream isLog = null;
		CommandLine cmd = null;
		Options opts = new Options();
		opts.addOption("h", "help", false, "help");
		opts.addOption("v", "version", false, "print the version information and exit");
		opts.addOption("m", null, true, "mdmhost");
		opts.addOption("r", null, true, "repository");
		opts.addOption("u", null, true, "user");
		opts.addOption("p", null, true, "password");

		try {
			// look for non-JAR properties
			File lp = new File("logging.properties");
			if (lp.isFile() && lp.exists())
				isLog = new FileInputStream(lp);
			else
				isLog = Main.class.getResourceAsStream("/com/pinternals/mdmr/logging.properties");
			if (isLog!=null) {
				LogManager.getLogManager().readConfiguration(isLog);
			} else {
				LogManager.getLogManager().readConfiguration();
			}
//			new LogFormatter(LogManager.getLogManager());
			log.finest("ALC is started");	
			cmd = new PosixParser().parse(opts, args);
			Report.logInfo("Main001start");
			if (cmd.hasOption("help") || 
					( ( cmd.getOptions()==null || cmd.getOptions().length==0 ) &&
					  ( cmd.getArgs()==null || cmd.getArgs().length==0) ) ) { 
				new HelpFormatter().printHelp( "java -jar mdmr.jar", opts, true );
				return;
			} else if (cmd.hasOption("version")) { 
				System.out.println("http://pinternals.com/mdmr version " + Report.version);
				System.out.println("MDM client API version " + MdmClient.getClientAPIversion());
			} else { // if (cmd.getArgs()!=null && cmd.getArgs().length>0 ) {
				String host = cmd.getOptionValue("m");
				String rep = cmd.getOptionValue("r");
				String region = MdmClient.locRU; 
				String repUser = cmd.getOptionValue("u");
				String repPasswd = cmd.getOptionValue("p");

				Report report = new Report();
				report.getInfo(host, rep, region, repUser, repPasswd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
