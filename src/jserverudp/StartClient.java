package jserverudp;

import jserverudp.client.Client;
import jserverudp.packets.PingPacket;

public class StartClient {
	
	public static void main(String[] args) {
		Client client = new Client("localhost", 222);
		client.logger.debug = false;
		client.logger.info(client.getPing()+"");
	}
}
