package jserverudp.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import carbonchat.api.fr.Logger;
import carbonchat.api.fr.LoggerFile;
import jserverudp.packets.Packet;
import jserverudp.packets.PacketObject;
import jserverudp.packets.PacketReponse;
import jserverudp.packets.PacketType;

public class Server implements Runnable {

	public final Logger logger = new Logger("Server",
			new OutputStream[] { System.out, new LoggerFile("logger").getOutputStream() });

	public boolean running = false;
	public DatagramSocket socket;
	public Thread serverThread;

	public Server(int port) {
		logger.debug = true;
		try {
			this.socket = new DatagramSocket(port);
			this.running = true;
			this.serverThread = new Thread(this, "Server Thread");
			this.serverThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		while (running) {
			logger.info("the server is listening to the port : " + this.socket.getLocalPort());
			try {
				while (running) {
					byte[] data = new byte[this.socket.getReceiveBufferSize()];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					socket.receive(packet);

					InetAddress adress = packet.getAddress();
					int port = packet.getPort();

					String packetData = new String(data);

					try {
						PacketObject packetObject = PacketObject.read(packetData);

						if (packetObject.getPacketType() == PacketType.SEND) {
							logger.debug("[" + adress.getHostAddress() + ":" + port + "] [RECEIVER] packetData : "
									+ packetData);
							Packet packet_ = (Packet) packetObject;
							packet_.handleFromServer(this, adress, port);
						} else if (packetObject.getPacketType() == PacketType.REPONSE) {
							logger.debug("[" + adress.getHostAddress() + ":" + port + "] [RECEIVER] packetData : "
									+ packetData);
							PacketReponse packet_ = (PacketReponse) packetObject;
							packet_.handleFromServer(this, adress, port);

							if (Packet.waitingsReponse
									.containsKey(packet_.getMasterPacket().getClass().getSimpleName())) {
								Packet packet__ = Packet.waitingsReponse
										.get(packet_.getMasterPacket().getClass().getSimpleName());
								logger.debug("[" + adress.getHostAddress() + ":" + port + "] [REPONSE] <"
										+ packet_.getMasterPacket().getClass().getSimpleName() + "> to <"
										+ packet_.getClass().getSimpleName() + ">");
								packet__.reponse.set(packet_);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (socket.isConnected() && !socket.isClosed())
				socket.close();
			logger.info("server close.");
		}
	}

	public void stop() {
		running = false;
	}

	public void sendPacket(PacketObject packet, InetAddress adress, int port) {
		send(packet.getData().getBytes(), adress, port);
		logger.debug("[" + adress.getHostAddress() + ":" + port + "] [SENDER] packetData : " + packet.getData());
	}

	public void send(byte[] data, InetAddress adress, int port) {
		new Thread("Send Packet") {
			@Override
			public void run() {
				try {
					DatagramPacket packet = new DatagramPacket(data, data.length, adress, port);
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
