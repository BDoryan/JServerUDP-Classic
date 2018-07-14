package jserverudp;

import jserverudp.server.Server;

public class StartServer {

	public static void main(String[] args) {
		Server server = new Server(111);
		server.logger.debug = false;
	}
}
