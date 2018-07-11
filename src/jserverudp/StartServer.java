package jserverudp;

import jserverudp.server.Server;

public class StartServer {

	public static void main(String[] args) {
		Server server = new Server(222);
		server.logger.debug = false;
	}
}
