package com.ekoplat.iot.server.common.model;

import java.io.Serializable;

public class ResponseCmd implements Serializable {

    private int HEAD;

    private short module;

    private short cmd;

    private int length;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    private byte[] data;

    public int getHEAD() {
        return HEAD;
    }

    public void setHEAD(int HEAD) {
        this.HEAD = HEAD;
    }

    public short getModule() {
        return module;
    }

    public void setModule(short module) {
        this.module = module;
    }

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getDataLength(){
        if(data == null){
            return 0;
        }
        return data.length;
    }
}
