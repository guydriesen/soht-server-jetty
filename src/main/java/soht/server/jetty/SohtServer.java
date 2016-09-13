package soht.server.jetty;

import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import lombok.Getter;

public class SohtServer {

	@Option(name = "-l", aliases = "--listen-addess", usage = "address to listen on")
	@Getter
	private String host = "ALL";

	@Option(name = "-p", aliases = "--port", usage = "port to listen on", metaVar = "INT")
	@Getter
	private int port = 8080;

	@Option(name = "-d", aliases = "--deploy-path", usage = "server deployment path")
	@Getter
	private String path = "/soht";

	@Option(name = "-h", aliases = "--help", usage = "prints this message", help = true)
	private void printHelp(boolean cleanExit) {
		parser.printUsage(System.err);
		System.exit(cleanExit ? 0 : 1);
	}

	CmdLineParser parser;

	private void parseArguments(String[] args) {
		parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			printHelp(false);
		}
	}

	public static void main(String[] args) throws Exception {
		// Use the ecj compiler to compile the jsp's
		System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

		SohtServer ss = new SohtServer();
		ss.parseArguments(args);

		Server server = new Server();

		ServerConnector serverConnector = new ServerConnector(server);
		if (!ss.getHost().equals("ALL")) {
			serverConnector.setHost(ss.getHost());
		}
		serverConnector.setPort(ss.getPort());
		server.addConnector(serverConnector);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath(ss.getPath());

		// Initialize the jsp engine
		JettyJasperInitializer sci = new JettyJasperInitializer();
		ContainerInitializer initializer = new ContainerInitializer(sci, null);
		List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
		initializers.add(initializer);
		webAppContext.setAttribute("org.eclipse.jetty.containerInitializers", initializers);

		webAppContext.addBean(new ServletContainerInitializersStarter(webAppContext), true);

		ProtectionDomain domain = SohtServer.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();
		webAppContext.setWar(location.toExternalForm());

		server.setHandler(webAppContext);
		server.start();
		server.join();
	}
}