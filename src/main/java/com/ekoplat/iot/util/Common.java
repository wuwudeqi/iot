package com.ekoplat.iot.util;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 16:43 2019-07-24
 **/
public class Common {

    /**
     * 返回设备ip
     * @param ipStr
     * @return ip
     */
    public static String getIP(String ipStr) {
        String[] ips = ipStr.split(":");
        return ips[0].substring(1);
    }
}
