package jserverudp;

import jserverudp.client.Client;

public class StartClient {
	
	public static void main(String[] args) {
		Client client = new Client("localhost", 111);
		client.logger.debug = false;
		System.out.println(client.getPing()+" ms");
	}
}
