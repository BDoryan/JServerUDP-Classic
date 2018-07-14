package jserverudp.packets;

public enum PacketType {

	SEND(1),
	REPONSE(2);

	int id;

	PacketType(int id) {
		this.id = id;
	}

	public static PacketType get(int id) {
		for (PacketType packetType : PacketType.values()) {
			if (packetType.id == id) {
				return packetType;
			}
		}
		return null;
	}
}
