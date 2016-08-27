package soht.server.jetty;

import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class SohtServer {

	public static void main(String[] args) throws Exception {
		// Use the ecj compiler to compile the jsp's
		System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

		// Default local bind address & port
		String host = "ALL"; // "localhost"
		int port = 8080;

		// Parse arguments
		if (args.length != 0) {
			StringTokenizer tokens = new StringTokenizer(args[0], ":");
			if (tokens.countTokens() == 1) {
				port = Integer.parseInt(tokens.nextToken());
			} else {
				host = tokens.nextToken();
				port = Integer.parseInt(tokens.nextToken());
			}
		}

		Server server = new Server();

		ServerConnector serverConnector = new ServerConnector(server);
		if (!host.equals("ALL")) {
			serverConnector.setHost(host);
		}
		serverConnector.setPort(port);
		server.addConnector(serverConnector);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");

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