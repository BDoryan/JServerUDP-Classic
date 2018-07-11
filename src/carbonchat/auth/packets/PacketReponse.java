package carbonchat.auth.packets;

import java.net.InetAddress;

import carbonchat.auth.client.Client;
import carbonchat.auth.server.Server;

public abstract class PacketReponse extends PacketObject {

	protected Packet masterPacket;
	
	public PacketReponse(Packet masterPacket, int id, Object[] datas) {
		super(PacketType.REPONSE, id, datas);
		this.masterPacket = masterPacket;
		Packet.add(this);
	}
	
	public abstract void handleFromServer(Server server, InetAddress adress, int port);
	public abstract void handleFromClient(Client client, InetAddress adress, int port);
	public abstract PacketReponse clone();
	
	public int getID() {
		return this.id;
	}

	public Packet getMasterPacket() {
		return masterPacket;
	}

	public static final int REPONSE_ERROR_ALREADY_IN_WAITING = 001;
	public static final int REPONSE_ERROR_NO_REPONSE_FOR_WAITING = 002;
	
	public static class PacketReponseError extends PacketReponse {
		
		public PacketReponseError(int ERROR_TYPE, String reason) {
			super(null, -1, new Object[] {ERROR_TYPE, reason});
		}
		
		public PacketReponseError() {
			super(null, -1, new Object[2]);
		}

		@Override
		public void handleFromServer(Server server, InetAddress adress, int port) {
		}

		@Override
		public void handleFromClient(Client client, InetAddress adress, int port) {
		}

		@Override
		public PacketReponse clone() {
			return new PacketReponseError();
		}

		public int getErrorType() {
			return this.datas[0].readInt();
		}

		public String getErrorReason() {
			return this.datas[1].readString();
		}
	}
}
