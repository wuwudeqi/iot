package com.ekoplat.iot.server.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseCmd implements Serializable {

    private int HEAD;

    private short module;

    private short cmd;

    private int length;

    private byte[] data;

    public int getDataLength(){
        if(data == null){
            return 0;
        }
        return data.length;
    }
}
