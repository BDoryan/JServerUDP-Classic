package jserverudp.packets;

public class PacketObject {

	public static final CharSequence PACKET_SEPARATOR = ";";

	public PacketType packetType;
	public int id;
	public Data[] datas;

	public PacketObject(PacketType packetType, int id, Object[] datas) {
		this.packetType = packetType;
		this.id = id;

		if (datas == null) {
			this.datas = null;
		} else {
			Data[] datas_ = new Data[datas.length];

			int position = 0;
			for (Object data : datas) {
				if (data instanceof String) {
					if (((String) data).contains(PACKET_SEPARATOR)) {
						new IllegalArgumentException(
								"you can't create packet with this char : '" + PACKET_SEPARATOR + "");
					}
					datas_[position] = new Data(data + "");
				} else if (data instanceof Integer) {
					datas_[position] = new Data((int) data);
				} else if (data instanceof Double) {
					datas_[position] = new Data((double) data);
				} else if (data instanceof Float) {
					datas_[position] = new Data((float) data);
				} else if (data instanceof Long) {
					datas_[position] = new Data((long) data);
				} else if (data instanceof Boolean) {
					datas_[position] = new Data((boolean) data);
				}
				position++;
			}
			this.datas = datas_;
		}
	}

	public static PacketObject read(String packetData) {
		long start = System.currentTimeMillis();
		try {
			String[] datas = packetData.split(PACKET_SEPARATOR+"");
			if (packetData.length() >= 2) {
				Integer packetID = Integer.valueOf(datas[0]);
				if(packetID == null) {
					System.err.println("packetID == null : "+packetData);
					return null;
				}
				PacketType packetType = PacketType.get(Integer.valueOf(datas[1].trim()));
				if(packetType == null) {
					System.err.println("packetType == null : "+packetData);
					return null;
				}
				Data[] packetDatas = null;
				if(datas.length > 2) {
					packetDatas = new Data[(datas.length - 2) + 1];
					for (int i = 0; i < datas.length; i++) {
						String data = datas[i + 2];
						if (data.startsWith("s=")) { // STRING
							packetDatas[i] = new Data(data);
						} else if (data.startsWith("i=")) { // INTEGER
							packetDatas[i] = new Data(Integer.valueOf(data));
						} else if (data.startsWith("d=")) { // DOUBLE
							packetDatas[i] = new Data(Double.valueOf(data));
						} else if (data.startsWith("f=")) { // FLOAT
							packetDatas[i] = new Data(Float.valueOf(data));
						} else if (data.startsWith("l=")) { // LONG
							packetDatas[i] = new Data(Long.valueOf(data));
						} else if (data.startsWith("b=")) { // BOOLEAN
							packetDatas[i] = new Data(Boolean.valueOf(data));
						} else {
							System.err.println("dataType not found (" + data + ")");
						}
					}	
				}
				
				PacketObject packet = null;
				if (packetType == PacketType.REPONSE) {
					if (Packet.packetsReponse.containsKey(packetID)) {
						packet = Packet.packetsReponse.get(packetID);

						packet.packetType = packetType;
						packet.id = packetID;
						packet.datas = packetDatas;
					} else {
						System.err.println("packet unknown !");
					}
				} else if (packetType == PacketType.SEND) {
					if (Packet.packets.containsKey(packetID)) {
						packet = Packet.packets.get(packetID);

						packet.packetType = packetType;
						packet.id = packetID;
						packet.datas = packetDatas;
					} else {
						System.err.println("packet unknown !");
					}
				} else {
					System.err.println("invalid 'datas.packetType' == " + packetType.toString());
				}
				System.out.println(packetID+" read latence : "+(System.currentTimeMillis() - start));
				return packet;
			} else {
				System.err.println("datas.length < 2 :" + packetData);
			}
		} catch (Exception e) {
			System.err.println("invalid char : " + packetData);
			e.printStackTrace();
		}
		return null;
	}

	public String getData() {
		long start = System.currentTimeMillis();
		String data = this.id + "" + PACKET_SEPARATOR + this.packetType.id;

		if(this.datas != null) {
			for (Data data_ : this.datas) {
				String stringData = null;
				if (data_.dataType == DataType.STRING) {
					stringData = PACKET_SEPARATOR + "s=" + data_.readString();
				} else if (data_.dataType == DataType.BOOLEAN) {
					stringData = PACKET_SEPARATOR + "b=" + data_.readBoolean();
				} else if (data_.dataType == DataType.DOUBLE) {
					stringData = PACKET_SEPARATOR + "d=" + data_.readDouble();
				} else if (data_.dataType == DataType.INTEGER) {
					stringData = PACKET_SEPARATOR + "i=" + data_.readInt();
				} else if (data_.dataType == DataType.FLOAT) {
					stringData = PACKET_SEPARATOR + "f=" + data_.readFloat();
				} else if (data_.dataType == DataType.LONG) {
					stringData = PACKET_SEPARATOR + "l=" + data_.readLong();
				} else {
					new Exception("dataType not found (" + data_.dataType + ")");
				}
				data += stringData;
			}	
		}

		System.out.println(id+" write latence : "+(System.currentTimeMillis() - start));
		return data;
	}

	public int getID() {
		return this.id;
	}

	public PacketType getPacketType() {
		return this.packetType;
	}

	public static class Data {
		protected DataType dataType;
		protected String data;

		public Data(String data) {
			this.dataType = DataType.STRING;
			this.data = data;
		}

		public Data(int data) {
			this.dataType = DataType.INTEGER;
			this.data = data + "";
		}

		public Data(boolean data) {
			this.dataType = DataType.BOOLEAN;
			this.data = data + "";
		}

		public Data(long data) {
			this.dataType = DataType.LONG;
			this.data = data + "";
		}

		public Data(float data) {
			this.dataType = DataType.FLOAT;
			this.data = data + "";
		}

		public Data(double data) {
			this.dataType = DataType.DOUBLE;
			this.data = data + "";
		}

		public String readString() {
			return this.data;
		}

		public int readInt() {
			return Integer.valueOf(this.data);
		}

		public long readLong() {
			return Long.valueOf(this.data);
		}

		public float readFloat() {
			return Float.valueOf(this.data);
		}

		public double readDouble() {
			return Double.valueOf(this.data);
		}

		public boolean readBoolean() {
			return Boolean.valueOf(this.data);
		}
	}

	public static enum DataType {
		STRING, INTEGER, BOOLEAN, DOUBLE, LONG, FLOAT;
	}
}
