/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cli;

import static db.OdbHsqldbBridge.*;
import org.hsqldb.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

/**
 * The command extract a database into a given directory or compress a database into a given file.
 * @author Arnaud Malapert</br> 
 * @since 20 oct. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public class HsqldbServerCmd extends AbstractCmdLine {
	// TODO - Simplify command line (only compress/uncompress) - created 5 juil. 2011 by Arnaud Malapert

	@Option(name="-d",aliases={"--dir"},usage="compress/uncompress to/from the given directory",required=true)
	protected File dbDir;	

	@Option(name="-n",aliases={"--name"},usage="Database name")
	protected String dbName= DBNAME;

	@Option(name="-t",aliases={"--target"},usage="compress the database into the given file.")
	protected File target;

	@Option(name="-p",aliases={"--pattern"},usage="Database pattern (open office database .odb)")
	protected File odb;

	@Option(name="-ns",aliases={"--noserver"},usage="Do not start the hsqldb server (extraction only). you should stop the server manually.")
	protected boolean noServer;

	@Option(name="-v",aliases={"--verbose"},usage="higher verbosity.")
	protected boolean verbose;


	private InputStream odbStream;

	public HsqldbServerCmd() {
		super(false);
	}



	@Override
	public void help() {
		System.err.println("\nThe command follows two modes according to the presence of --target argument:");
		System.err.println("\t-If absent, extract the database from the odb file and start a server.");
		System.err.println("\t-If present, compress the database from the given directory and create a new file with a given pattern.");
		super.help();
	}



	@Override
	protected void checkData() throws CmdLineException {
		if(verbose) LOGGER.setLevel(Level.ALL);
		//check directory and file
		if( target == null ) {
			//extraction to dbdir
			if( ! dbDir.exists() && ! dbDir.mkdirs()) throw new CmdLineException("cant create directory "+dbDir);
			if( ! dbDir.canWrite()) throw new CmdLineException(dbDir+" is not writable.");
		} else {
			if ( ! dbDir.canRead()) throw new CmdLineException(dbDir+" is not readable.");	//compression
			if( target.exists()) throw new CmdLineException(target+" already exists.");
		}

		//check odb pattern
		if( odb == null) odbStream = getDefaultOdbPattern(this);
		else {
			try {
				odbStream = new FileInputStream(odb);
			} catch (FileNotFoundException e) {
				throw new CmdLineException("invalid odb pattern file.", e);
			}
		}
	}

	protected void displayConnectionUrls() {
		if(LOGGER.isLoggable(Level.INFO)) {
			final StringBuilder b  =new StringBuilder(128);
			b.append("\nconnection to the database using jdbc:");
			b.append("\n\t-Embedded: ").append(makeEmbeddedURL(dbDir, dbName));
			b.append("\n\t-Server:\n\t\t").append(makeNetworkURL("localhost", dbName));
			try {
				Enumeration<NetworkInterface> en =	NetworkInterface.getNetworkInterfaces();
				while(en.hasMoreElements()) {
					final List<InterfaceAddress> inetAdresses = en.nextElement().getInterfaceAddresses();
					b.append("\n\t\t").append(makeNetworkURL(inetAdresses.get(inetAdresses.size()-1).getAddress().toString(), dbName));
				}
			} catch (SocketException e) {
				LOGGER.log(Level.WARNING, "cant display network connection urls",e);
			}
			LOGGER.info(b.toString());
		}
	}

	protected Server startHsqldbServer() {
		Server server = new Server();
		server.setDatabaseName(0, dbName);
		server.setDatabasePath( 0, dbDir.getAbsolutePath()+ '/' +dbName);
		server.setLogWriter(null);
		server.setErrWriter(null);
		server.start();
		return server;
	}

	@Override
	protected void execute() {
		try {
			if( target == null) {
				//extract
				try {
					uncompressDatabase( odbStream, dbDir, dbName);
					if( ! noServer ) {
						startHsqldbServer();
					}else LOGGER.info("hsqldb...[NO_SERVER]");
					displayConnectionUrls();
				} catch (FileNotFoundException e) {
					exitOnException(e);
				}
			} else {
				//compress
				exportDatabase(odbStream, dbDir, dbName, target);
			}
		} catch (IOException e) {
			exitOnException(e);
		}
	}

	public static void main(String[] args) {
		new HsqldbServerCmd().doMain(args);
	}
}