package jserverudp.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import carbonchat.api.fr.Logger;
import jserverudp.packets.Packet;
import jserverudp.packets.PacketObject;
import jserverudp.packets.PacketReponse;
import jserverudp.packets.PacketType;
import jserverudp.packets.PingPacket;

public class Client implements Runnable {

	public final Logger logger = new Logger("Client", new OutputStream[] { System.out });

	public boolean running = false;
	public DatagramSocket socket;
	public InetAddress inetAdress;
	public int port;
	public Thread clientThread;

	public Client(String adress, int port) {
		logger.debug = true;
		try {
			this.socket = new DatagramSocket();
			try {
				this.inetAdress = InetAddress.getByName(adress);
				this.port = port;
				logger.info("connection on server with success :D");
				this.running = true;
				this.clientThread = new Thread(this, "Client Thread");
				this.clientThread.start();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void startCheckPingTime(int duration) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				getPing();
			}
		}, 0, duration);
	}

	public Long getPing() {
		PingPacket pingPacket = new PingPacket();
		sendPacket(pingPacket);

		long startOff = System.currentTimeMillis();
		PacketReponse reponse = Packet.waitReponse(pingPacket, 2000);

		if (reponse.getID() == -1) {
			PacketReponse.PacketReponseError errorPacket = (PacketReponse.PacketReponseError) reponse;
			if (errorPacket.getErrorType() == PacketReponse.REPONSE_ERROR_NO_REPONSE_FOR_WAITING) {
				logger.error("[getPing()] any reponse by server for client!");
				deconnection();
			}
		} else {
			return System.currentTimeMillis() - startOff;
		}
		return null;
	}

	@Override
	public void run() {
		logger.info("the client is ready :D");
		try {
			while (running) {
				byte[] data = new byte[1024];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				this.socket.receive(packet);

				InetAddress adress = packet.getAddress();
				int port = packet.getPort();

				String packetData = new String(data);

				try {
					JsonReader reader = new JsonReader(new StringReader(packetData));
					PacketObject packetObject = new Gson().fromJson(reader, PacketObject.class);

					if (packetObject.getPacketType() == PacketType.SEND) {
						int packetID = packetObject.getID();
						logger.debug(
								"[" + adress.getHostAddress() + ":" + port + "] [RECEIVER] packetData : " + packetData);
						if (Packet.packets.containsKey(packetID)) {
							Packet packet_ = Packet.packets.get(packetID).clone();
							packet_.read(packetObject);
							packet_.handleFromClient(this, adress, port);

						} else {
							logger.error("packet unknown : " + packetData);
						}
					} else if (packetObject.getPacketType() == PacketType.REPONSE) {
						int packetID = packetObject.getID();
						logger.debug(
								"[" + adress.getHostAddress() + ":" + port + "] [RECEIVER] packetData : " + packetData);
						if (Packet.packetsReponse.containsKey(packetID)) {
							PacketReponse packet_ = Packet.packetsReponse.get(packetID).clone();
							packet_.read(packetObject);
							packet_.handleFromClient(this, adress, port);
							
							if (Packet.waitingsReponse
									.containsKey(packet_.getMasterPacket().getClass().getSimpleName())) {
								Packet packet__ = Packet.waitingsReponse
										.get(packet_.getMasterPacket().getClass().getSimpleName());
								logger.debug("[" + adress.getHostAddress() + ":" + port + "] [REPONSE] <"
										+ packet_.getMasterPacket().getClass().getSimpleName() + "> to <"
										+ packet_.getClass().getSimpleName() + ">");
								packet__.reponse.set(packet_);
							}
						} else {
							logger.error("packet  reponse unknown : " + packetData);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		running = false;
		if (socket.isConnected() && !socket.isClosed())
			socket.close();
		logger.info("client disconnected.");
	}

	public void deconnection() {
		running = false;
	}

	public void sendPacket(PacketObject packet) {
		send(packet.getData().getBytes());
		logger.debug("[" + inetAdress.getHostAddress() + ":" + port + "] [SENDER] packetData : " + packet.getData());
	}

	public void send(byte[] data) {
		if (socket == null || !socket.isConnected() && socket.isClosed()) {
			logger.error("client offline!");
			return;
		}
		new Thread("Send Packet") {
			@Override
			public void run() {
				try {
					DatagramPacket packet = new DatagramPacket(data, data.length, inetAdress, port);
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
