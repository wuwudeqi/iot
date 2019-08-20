package com.ekoplat.iot.server.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ekoplat.iot.dataobject.GatewayAndLock;
import com.ekoplat.iot.location.DeviceLocation;
import com.ekoplat.iot.server.common.model.RequestCmd;
import com.ekoplat.iot.service.GatewayAndLockService;
import com.ekoplat.iot.util.Common;
import com.ekoplat.iot.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JsonMsgHandler extends ChannelInboundHandlerAdapter {

    private GatewayAndLockService gatewayAndLockService = SpringUtil.getBean(GatewayAndLockService.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String ip = Common.getIP(ctx.channel().remoteAddress().toString());
        if (msg instanceof String) {
            String jsonMsg = (String) msg;
            JSONObject json = JSON.parseObject(jsonMsg);
            String gwId = json.getString("GW-ID");
            Integer type = json.getInteger("type");
            switch (type) {
                case 0:
                    //将ip作为键存入map
                    ChannelMap.addChannel(ip, ctx.channel());
                    gatewayAndLockService.changeGwStatus(gwId, ip, 1);
                    break;
                case 1:
                    String onlockId = json.getString("ADD-OK");
                    if (!"".equals(onlockId)) {
                        gatewayAndLockService.bindLock(gwId, onlockId);
                    }
                    break;
                case 2:
                    String offlockId = json.getString("DEL-DEV");
                    gatewayAndLockService.unbindLock(gwId, offlockId);
                    break;
                case 30:
                    //将ip作为键存入map
                    ChannelMap.addChannel(ip, ctx.channel());
                    int mnc = json.getInteger("MNC");
                    int lac = json.getInteger("LAC");
                    int ci = json.getInteger("CI");
                    String address = null;
                    try {
                        address = DeviceLocation.load(mnc, lac, ci);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    log.info("【设备定位】id={} address={}",gwId,address);
                    JSONObject versionJson = json.getJSONObject("version");
                    String gatewayVersion = versionJson.getString("gateway");
                    String lockVersion = versionJson.getString("lock");
                    GatewayAndLock gatewayAndLock = gatewayAndLockService.findBygwId(gwId);
                    if (gatewayAndLock == null) {
                        gatewayAndLock = new GatewayAndLock();
                        gatewayAndLock.setIp(ip);
                        gatewayAndLock.setGwId(gwId);
                    }
                    gatewayAndLock.setAddress(address);
                    gatewayAndLock.setGwVersion(gatewayVersion);
                    gatewayAndLock.setLockVersion(lockVersion);
                    gatewayAndLockService.save(gatewayAndLock);
                    gatewayAndLockService.changeGwStatus(gwId, ip, 1);
                    break;
                case 31:
                    String LockStatusId = json.getString("DEV-ID");
                    Integer online = json.getInteger("online");
                    GatewayAndLock gwLock = gatewayAndLockService.findByLockId(LockStatusId);
                    gwLock.setLockStatus(online);
                    gatewayAndLockService.save(gwLock);
                    log.info("【锁的状态】lockId:{}, lockStatus:{}", LockStatusId, online);
            }

        } else if (msg instanceof RequestCmd){
            ctx.fireChannelRead(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("【MSG】" + cause.toString() + "ip:" + Common.getIP(ctx.channel().remoteAddress().toString()));
    }
}
