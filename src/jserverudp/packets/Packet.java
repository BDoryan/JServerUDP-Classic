package jserverudp.packets;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import jserverudp.client.Client;
import jserverudp.server.Server;

public abstract class Packet extends PacketObject {
	
	public AtomicReference<PacketReponse> reponse;
	
	public Packet(int id, Object[] datas) {
		super(PacketType.SEND, id, datas);
		add(this);
	}
	
	public abstract void handleFromServer(Server server, InetAddress adress, int port);
	public abstract void handleFromClient(Client client, InetAddress adress, int port);
	public abstract Packet clone();

	public static final HashMap<Integer, Packet> packets = new HashMap<>();
	public static final HashMap<Integer, PacketReponse> packetsReponse = new HashMap<>();
	
	public static final HashMap<String, Packet> waitingsReponse = new HashMap<>();

	public static void add(Packet packet) {
		if (packets.containsKey(packet.id))
			return;
		packets.put(packet.id, packet);
	}

	public static void add(PacketReponse packet) {
		if (packetsReponse.containsKey(packet.id))
			return;
		packetsReponse.put(packet.id, packet);
	}

	static {
		new PingPacket();
		new PongPacket();
	}

	public int getID() {
		return this.id;
	}

	public static PacketReponse waitReponse(Packet packet, long timeoff) {
		if(waitingsReponse.containsKey(packet.getClass().getSimpleName())) {
			return new PacketReponse.PacketReponseError(PacketReponse.REPONSE_ERROR_ALREADY_IN_WAITING, "This packet is already waiting for reponse");
		}
		waitingsReponse.put(packet.getClass().getSimpleName(), packet);
		packet.reponse = new AtomicReference<PacketReponse>();
		long time = System.currentTimeMillis() + timeoff;
		while(packet.reponse == null || packet.reponse.get() == null) {
			if((time - System.currentTimeMillis()) < 0L)
				return new PacketReponse.PacketReponseError(PacketReponse.REPONSE_ERROR_NO_REPONSE_FOR_WAITING, "any packet of reponse");
		}
		waitingsReponse.remove(packet.getClass().getSimpleName());
		PacketReponse packetReponse = packet.reponse.get();
		packet.reponse.set(null);
		return packetReponse;
	}
}
