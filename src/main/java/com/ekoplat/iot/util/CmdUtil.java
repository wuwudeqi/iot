package com.ekoplat.iot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ekoplat.iot.server.common.constant.Head;
import com.ekoplat.iot.server.common.constant.ModuleAndCmd;
import com.ekoplat.iot.server.common.model.ResponseCmd;
import io.netty.channel.Channel;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;


/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 10:50 2019-08-01
 **/
public class CmdUtil {

    /**
     * 主动升级发送信号给硬件
     * @param jsonStr
     * @param ch
     */
    public static void sendActiveUpdate(String jsonStr, Channel ch) throws DecoderException {
        JSONObject json = JSON.parseObject(jsonStr);
        ResponseCmd response = new ResponseCmd();
        response.setHEAD(Head.FLAG);
        response.setModule(ModuleAndCmd.H_Active_Module);
        response.setCmd(ModuleAndCmd.H_Active_Cmd);
        String code = (String) json.get("code");
        if( !"fail".equals(code) && code !=null){
            byte[] typeVersionBytes = Hex.decodeHex(code);
            byte[] data = new byte[14+typeVersionBytes.length];
            response.setLength(data.length);
            byte[] gwIds = ((String) json.get("gwId")).getBytes();
            System.arraycopy(gwIds,0,data,0,14);
            System.arraycopy(typeVersionBytes,0,data,14,typeVersionBytes.length);
            response.setData(data);
            ch.writeAndFlush(response);
        }
    }
}
