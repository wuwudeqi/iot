package com.ekoplat.iot.enums;

import lombok.Getter;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 11:13 2019-07-12
 **/
@Getter
public enum ResultEnum {
    GW_OFF(0,"网关不在线"),
    GW_NOCONNECT(1, "网关从未连接"),
    PACKAGE_ERROR(2, "请先在网页主动升级"),
    TYPENUM_NOTEXEIST(3, "设备类型在数据库不存在")
    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
