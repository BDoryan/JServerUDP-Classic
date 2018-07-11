package jserverudp.packets;

import java.net.InetAddress;

import jserverudp.client.Client;
import jserverudp.server.Server;

public class PingPacket extends Packet {
	
	public static final int packetID = 001;

	public PingPacket() {
		super(packetID, null);
	}
	
	@Override
	public Packet clone() {
		return new PingPacket();
	}

	@Override
	public void handleFromServer(Server server, InetAddress adress, int port) {
		server.sendPacket(new PongPacket(), adress, port);
	}

	@Override
	public void handleFromClient(Client client, InetAddress adress, int port) {
	}
}