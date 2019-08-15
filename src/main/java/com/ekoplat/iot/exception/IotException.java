package com.ekoplat.iot.exception;


import com.ekoplat.iot.enums.ResultEnum;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 11:12 2019-07-12
 **/
public class IotException extends  RuntimeException{

    private Integer code;

    public IotException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public IotException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public IotException(String message) {
        super(message);
    }

}
