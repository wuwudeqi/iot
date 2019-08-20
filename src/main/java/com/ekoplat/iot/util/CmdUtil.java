package com.ekoplat.iot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ekoplat.iot.server.common.constant.Head;
import com.ekoplat.iot.server.common.constant.ModuleAndCmd;
import com.ekoplat.iot.server.common.model.ResponseCmd;
import com.ekoplat.iot.server.handler.ChannelMap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 10:50 2019-08-01
 **/
@Slf4j
public class CmdUtil {

    /**
     * 主动升级发送信号给硬件
     * @param gwLock_success_Map
     */
    public static void sendActiveUpdateCmd(Map<String,String> gwLock_success_Map) throws DecoderException {
        List list = new ArrayList<ResponseCmd>();
        for (String ip : gwLock_success_Map.keySet()) {
            JSONObject json = JSON.parseObject((String) gwLock_success_Map.get(ip));
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
                Channel ch = ChannelMap.getChannelByName(ip);
                if (ch == null) {
                    log.error("【信号发送错误】id为{},网络链路不存在",json.getString("gwId"));
                }
                ch.writeAndFlush(response);
                log.info("【信号发送成功】{}",response);
            }
        }
    }
}
