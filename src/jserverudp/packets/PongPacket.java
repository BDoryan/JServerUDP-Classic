package jserverudp.packets;

import java.net.InetAddress;

import jserverudp.client.Client;
import jserverudp.server.Server;

public class PongPacket extends PacketReponse {

	public static final int packetID = 002;
	
	public PongPacket() {
		super(new PingPacket(), packetID, null);
	}
	
	@Override
	public PacketReponse clone() {
		return new PongPacket();
	}
	
	@Override
	public void handleFromServer(Server server, InetAddress adress, int port) {
	}

	@Override
	public void handleFromClient(Client client, InetAddress adress, int port) {
		client.logger.info("pong reponse");
	}
}