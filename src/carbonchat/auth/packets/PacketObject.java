package carbonchat.auth.packets;

import com.google.gson.Gson;

public class PacketObject {

	public PacketType packetType;
	public int id;
	public Data[] datas;

	public PacketObject(PacketType packetType, int id, Object[] datas) {
		this.packetType = packetType;
		this.id = id;
		
		if(datas == null) {
			 this.datas = null;
		} else {
			Data[] datas_ = new Data[datas.length]; 
			
			int position = 0;
			for(Object data : datas) {
				if(data instanceof String) {
					datas_[position] = new Data(data+"");
				} else if(data instanceof Integer) {
					datas_[position] = new Data((int)data);
				} else if(data instanceof Double) {
					datas_[position] = new Data((double)data);
				} else if(data instanceof Float) {
					datas_[position] = new Data((float)data);
				} else if(data instanceof Long) {
					datas_[position] = new Data((long)data);
				} else if(data instanceof Boolean) {
					datas_[position] = new Data((boolean)data);
				}
				position++;
			}
			this.datas = datas_;	
		}
	}

	public void read(PacketObject packetObject) {
		this.packetType = packetObject.packetType;
		this.id = packetObject.id;
		this.datas = packetObject.datas;
	}

	public String getData() {
		return new Gson().toJson(this);
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
			this.data = data+"";
		}

		public Data(boolean data) {
			this.dataType = DataType.BOOLEAN;
			this.data = data+"";
		}
		
		public Data(long data) {
			this.dataType = DataType.LONG;
			this.data = data+"";
		}
		
		public Data(float data) {
			this.dataType = DataType.FLOAT;
			this.data = data+"";
		}
		
		public Data(double data) {
			this.dataType = DataType.DOUBLE;
			this.data = data+"";
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
		STRING,
		INTEGER,
		BOOLEAN,
		DOUBLE,
		LONG,
		FLOAT;
	}
}
