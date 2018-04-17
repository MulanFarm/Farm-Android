package com.p2p.shake;

import android.R.array;
import android.util.Log;
import android.widget.Button;

import com.p2p.core.global.Config;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;

public class ShakeData {
	public static class Cmd {
		public static final int GET_DEVICE_LIST = 1;
		public static final int RECEIVE_DEVICE_LIST = 2;
		public static final int NO_ASSOCIATED_DATA_PACKET = 9999;

		public static int[] getCmds() {
			return new int[] { 1, 2 };
		}
	}

	public static final int DEFAULT_LEFT_LENGTH = 28;
	public static final int DEFAULT_RIGHT_COUNT = 0;
	public static final int DEFAULT_STRING_PARAMETER_LENGTH = 64;
	public static int[] iCustomer = Config.AppConfig.CustomerIDs;
	// left 512 data
	private int cmd;
	private int error_code;
	private int leftlength;
	private int rightCount;
	private int id;
	private int type;
	private int flag;
	private int subType;


	// right 512 data
	private String name;

	public static byte[] getBytes(ShakeData data) {
		if (data.getLeftlength() == 0) {
			data.setLeftlength(DEFAULT_LEFT_LENGTH);
		}

		if (data.getRightCount() == 0) {
			data.setRightCount(DEFAULT_RIGHT_COUNT);
		}

		byte[] bytes;
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.putInt(data.getCmd());
		buffer.putInt(data.getError_code());
		buffer.putInt(data.getLeftlength());
		buffer.putInt(data.getRightCount());
		buffer.putInt(data.getId());
		buffer.putInt(data.getType());
		buffer.putInt(data.getFlag());
		buffer.putInt(data.getSubType());


		// putString(buffer,data.getName(),0);

		bytes = buffer.array();
		buffer.clear();
		return bytes;
	}

	public static ShakeData getShakeData(ByteBuffer buffer) {
		ShakeData data = new ShakeData();
		try {
			boolean isTrue = false;
			for (Integer cmd : Cmd.getCmds()) {
				if (buffer.getInt(0) == cmd) {
					isTrue = true;
					break;
				}
			}

			if (!isTrue) {
				throw new Exception();
			}
			buffer.limit(200);
			data.setCmd(buffer.getInt(0));
			data.setError_code(buffer.getInt(4));
			data.setLeftlength(buffer.getInt(8));
			data.setRightCount(buffer.getInt(12));
			data.setId(buffer.getInt(16));
			data.setType(buffer.getInt(20));
			data.setFlag(buffer.getInt(24));
			data.setSubType(buffer.getInt(80));
//			data.setFlag((int)buffer.get(24));
			int version=buffer.getInt(25);
			int  curVersion=(version>>5)&0x1;
//			data.setCurVersion(curVersion);
			
			
			boolean isCustomID=false;
			for(int i=0;i<iCustomer.length;i++){
				int customerID=iCustomer[i];
				if(customerID!=0){
					if ((buffer.getInt(12) & 0x01) != 0
							&& buffer.getInt(17 * 4)== customerID) {
						isCustomID=true;
						break;
					}
				}else{
					isCustomID=true;
					break;
				}
			}
			if(!isCustomID){
				data.setCmd(Cmd.NO_ASSOCIATED_DATA_PACKET);
			}
			// if (iCustomerID != 0) {
			// if ((buffer.getInt(12) & 0x01) == 0
			// || buffer.getInt(17 * 4) != iCustomerID) {
			// data.setCmd(Cmd.NO_ASSOCIATED_DATA_PACKET);
			// }
			// }

			// data.setName(getString(buffer,0));
		} catch (Exception e) {
			data.setCmd(Cmd.NO_ASSOCIATED_DATA_PACKET);
		}
		return data;
	}

	public static int putString(ByteBuffer buffer, String data, int index) {
		int position = 512 + index * DEFAULT_STRING_PARAMETER_LENGTH;
		int i = 0;
		byte[] bytes = data.getBytes();
		for (; i < bytes.length; i++) {
			if (i >= (DEFAULT_STRING_PARAMETER_LENGTH - 1)) {
				break;
			}
			buffer.put((position + i), bytes[i]);
		}

		buffer.put((index + DEFAULT_STRING_PARAMETER_LENGTH), (byte) 0);
		return index + DEFAULT_STRING_PARAMETER_LENGTH;
	}

	public static String getString(ByteBuffer buffer, int index) {
		String data = "";
		byte[] bytes = buffer.array();
		int position = 512 + index * DEFAULT_STRING_PARAMETER_LENGTH;
		for (int i = 0; i < DEFAULT_STRING_PARAMETER_LENGTH; i++) {
			if (bytes[position + i] == 0) {
				System.out.println(position + ":" + (position + i));
				data = new String(bytes, position, i);
				break;
			}
		}
		return data;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public int getLeftlength() {
		return leftlength;
	}

	public void setLeftlength(int leftlength) {
		this.leftlength = leftlength;
	}

	public int getRightCount() {
		return rightCount;
	}

	public void setRightCount(int rightCount) {
		this.rightCount = rightCount;
	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}
	
}
