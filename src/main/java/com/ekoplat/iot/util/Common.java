package com.ekoplat.iot.util;

import java.util.Date;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 16:43 2019-07-24
 **/
public class Common {

    /**
     * 返回设备ip
     *
     * @param ipStr
     * @return ip
     */
    public static String getIP(String ipStr) {
        String[] ips = ipStr.split(":");
        return ips[0].substring(1);
    }

    public static String getUseTime(Date end, Date begin) {
        //SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long between = 0;
        try {

            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                - min * 60 * 1000 - s * 1000);
        if (hour!=0){
            return  hour + "h" + min + "n" + s + "." + ms + "s";
        }else if(min!=0){
            return  min + "m" + s + "." + ms + "s";
        }else {
            return    s + "." + ms + "s";
        }
    }
}
