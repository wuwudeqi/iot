package com.ekoplat.iot.server.common.model;

import java.io.Serializable;

/**
 * 返回对象
 * @author -琴兽-
 *
 */
public class ResponsePackage implements Serializable {

	/**
	 * 包头
	 */
	private int HEAD;

	/**
	 * 请求模块
	 */
	private int module;
	
	/**
	 * 命令号
	 */
	private short cmd;
	
	/**
	 * 状态码
	 */
	private int stateCode;

	/**
	 * 包数
 	 */
	private int packageNum;
	
	/**
	 * 数据部分
	 */
	private byte[] data;
	
	/**
	 * crc校验码
	 */
	private short code;
	

	public short getCode() {
		return code;
	}

	public void setCode(short code) {
		this.code = code;
	}

	public int getModule() {
		return module;
	}

	public void setModule(int module) {
		this.module = module;
	}

	public short getCmd() {
		return cmd;
	}

	public void setCmd(short cmd) {
		this.cmd = cmd;
	}

	public int getStateCode() {
		return stateCode;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(int packageNum) {
		this.packageNum = packageNum;
	}

	public int getHEAD() {
		return HEAD;
	}

	public void setHEAD(int HEAD) {
		this.HEAD = HEAD;
	}

	public int getDataLength(){
		if(data == null){
			return 0;
		}
		return data.length;
	}


}
