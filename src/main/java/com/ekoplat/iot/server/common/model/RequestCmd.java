package com.ekoplat.iot.server.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 请求对象
 *
 */
@Data
public class RequestCmd implements Serializable {
	
	/**
	 * 请求模块
	 */
	private short module;
	
	/**
	 * 命令号
	 */
	private short cmd;
	
	/**
	 * 数据部分
	 */
	private byte[] data;

	
	public int getDataLength(){
		if(data == null){
			return 0;
		}
		return data.length;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("\"module\":")
				.append(module);
		sb.append(",\"cmd\":")
				.append(cmd);
		sb.append(",\"data\":")
				.append(Arrays.toString(data));
		sb.append('}');
		return sb.toString();
	}
}
