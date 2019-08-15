package com.ekoplat.iot.server.handler;


import com.ekoplat.iot.controller.FileUploadController;
import com.ekoplat.iot.dataobject.PackageInfo;
import com.ekoplat.iot.enums.ResultEnum;
import com.ekoplat.iot.exception.IotException;
import com.ekoplat.iot.repository.PackageRepository;
import com.ekoplat.iot.server.common.constant.Head;
import com.ekoplat.iot.server.common.model.RequestCmd;
import com.ekoplat.iot.server.common.model.ResponsePackage;
import com.ekoplat.iot.server.common.model.ResponseCmd;
import com.ekoplat.iot.server.common.model.StateCode;
import com.ekoplat.iot.util.CrcUtil;
import com.ekoplat.iot.util.FileUtil;
import com.ekoplat.iot.util.SpringUtil;
import com.ekoplat.iot.util.StringAndByte;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class HexHandler extends ChannelInboundHandlerAdapter {


    private PackageRepository repository = SpringUtil.getBean(PackageRepository.class);



    public ResponsePackage responsePackage = new ResponsePackage();
    public ResponseCmd responseCmd = new ResponseCmd();
    public int module;

    public String id;
    public String typeNum;
    public String version;
    public byte[] packageBytes;


    @Value("${param.gateway.typeNum}")
    private String gwHexTypeNum = "0000";

    @Value("${param.lock.typeNum}")
    private String lockHexTypeNum = "0101";




    public HexHandler() throws IOException {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String remoteAddre = ctx.channel().remoteAddress().toString();
        int colonAt = remoteAddre.indexOf(":");
        String ip = remoteAddre.substring(1, colonAt);
        /**
         * 16进制校验
         */
        if (msg instanceof RequestCmd) {
            RequestCmd message = (RequestCmd) msg;
            if (message.getModule() == 1) {
                //设置升级包包头
                responsePackage.setHEAD(Head.FLAG);
                /**
                 * 校验升级的设备
                 */
                if (message.getCmd() == 0) {
                    log.info("【升级校验】校验设备的类型");
                    byte[] data = message.getData();
                    byte[] typeNumBytes = new byte[]{
                            data[0],data[1]
                    };
                    String type = Hex.encodeHexString(typeNumBytes);
                    //根据类型查询
                    PackageInfo packgeInfo = repository.findFirstBytypeNumOrderByIdDesc(type);
                    if (packgeInfo == null) {
                        log.error("【类型错误】typeNum:{}", type);
                    }
                    log.info("【最新版本】{}", packgeInfo.toString());
                    int versionHead = data[2];
                    int versionTail = data[3];
                    String version = String.valueOf(versionHead) + "." + String.valueOf(versionTail);
                    //响应给硬件
                    responseCmd.setHEAD(Head.FLAG);
                    responseCmd.setModule((short) 4097);
                    byte[] byteDemo = new byte[4];
                    responseCmd.setData(byteDemo);
                    responseCmd.setLength(4);
                    if (packgeInfo.getVersion().compareTo(version) > 0) {
                        log.info("有新版本,需更新");
                        String fileName = FileUtil.generateFileName(packgeInfo.getTypeName(), packgeInfo.getVersion());
                        FileUploadController  fileUploadController = SpringUtil.getBean(FileUploadController.class);
                        String filePath = fileUploadController.filePath + fileName;
                        this.packageBytes = FileUtil.getBytes(filePath);
                        String[] updataIds = packgeInfo.getVersion().split("\\.");
                        //设置返回类型
                        byteDemo[0] = data[0];
                        byteDemo[1] = data[1];
                        //设置返回更新版本
                        int big = Integer.valueOf(updataIds[0]);
                        byteDemo[2] = (byte) big;
                        int little = Integer.valueOf(updataIds[1]);
                        byteDemo[3] = (byte) little;
                        responseCmd.setData(byteDemo);
                        log.info("升级包选择成功");
                        responseCmd.setCmd((short) 1);
                    } else if (packgeInfo.getVersion().compareTo(version) == 0) {
                        log.info("与服务器版本一致不需要更新");
                        responseCmd.setCmd((short) 2);
                    } else {
                        log.info("硬件版本比服务器高，不需要升级");
                        responseCmd.setCmd((short) 2);
                    }
                    ctx.channel().writeAndFlush(responseCmd);
                }
                /**
                 * 接受到正确包的返回
                 */
                else if (message.getCmd() == 1) {
                    log.info("success " + ip + " " + module + " " + StringAndByte.toHexString1(message.getData()));
                    int datalength = 512;
                    int length = packageBytes.length;
                    //设置包数
                    if (length % datalength == 0) {
                        responsePackage.setPackageNum(length / datalength);
                    } else {
                        responsePackage.setPackageNum(length / datalength + 1);
                    }
                    if (length - module * datalength > datalength) {
                        byte[] update = new byte[datalength];
                        responsePackage.setModule(module);
                        responsePackage.setCmd((short) 4369);
                        responsePackage.setStateCode(StateCode.SUCCESS);
                        int j = 0;
                        for (int i = 0; i < datalength; i++) {
                            update[j] = packageBytes[module * datalength + i];
                            j++;
                        }
                        responsePackage.setData(update);
                        responsePackage.setCode((short) CrcUtil.CRC16(update, datalength));
                        log.info("success " + ip + ": " + module + " " + StringAndByte.toHexString1(update));
                        ctx.channel().writeAndFlush(responsePackage);
                        module++;
                    } else {
                        responsePackage.setModule(module);
                        responsePackage.setCmd((short) 4369);
                        responsePackage.setStateCode(StateCode.OVER);
                        byte[] last = new byte[length - module * datalength];
                        int j = 0;
                        for (int i = 0; i < length - module * datalength; i++) {
                            last[j] = packageBytes[module * datalength + i];
                            j++;
                        }
                        responsePackage.setData(last);
                        responsePackage.setCode((short) CrcUtil.CRC16(last, length - module * datalength));
                        log.info("success " + ip + ": " + module + " " + StringAndByte.toHexString1(last));
                        ctx.channel().writeAndFlush(responsePackage);
                        module = 0;
                    }
                } else if (message.getCmd() == 2) {
                    log.info("fail " + ip + " " + module + " " + StringAndByte.toHexString1(message.getData()));
                    ctx.channel().writeAndFlush(responsePackage);
                    log.info("fail " + ip + ": " + module + " " + StringAndByte.toHexString1(responsePackage.getData()));
                }
            } else if (message.getModule() == 2) {
                responsePackage.setHEAD(Head.FLAG);

                if(message.getCmd() == 3) {
                    byte[] data = message.getData();
                    byte[] idBytes = new byte[14];
                    System.arraycopy(data,0,idBytes, 0, 14);
                    id = StringAndByte.hexStringToString(StringAndByte.bytesToHexString(idBytes));
                    typeNum = Hex.encodeHexString(new byte[]{data[14]},false) + Hex.encodeHexString(new byte[]{data[15]},false);
                    version = String.valueOf((int)data[16]) + "."+String.valueOf((int)data[17]);
                    PackageInfo packgeInfo = repository.findFirstBytypeNumOrderByIdDesc(typeNum);
                    FileUploadController  fileUploadController = SpringUtil.getBean(FileUploadController.class);
                    String filePath = fileUploadController.filePath + packgeInfo.getTypeName()+"_v"+packgeInfo.getVersion()+".bin";
                    this.packageBytes = FileUtil.getBytes(filePath);
                    log.info("【批量升级】升级包选择成功");
                    if( packageBytes == null) {
                        throw new IotException(ResultEnum.PACKAGE_ERROR);
                    }
                }
                if (message.getCmd() == 0) {
                    log.info("不需要升级");
                } else if (message.getCmd() == 1) {
                    log.info("success " + ip + " " + module + " " + StringAndByte.toHexString1(message.getData()));
                    int datalength = 512;
                    int length = packageBytes.length;
                    //设置包数
                    if (length % datalength == 0) {
                        responsePackage.setPackageNum(length / datalength);
                    } else {
                        responsePackage.setPackageNum(length / datalength + 1);
                    }
                    if (length - module * datalength > datalength) {
                        byte[] update = new byte[datalength];
                        responsePackage.setModule(module);
                        responsePackage.setCmd((short) 4369);
                        responsePackage.setStateCode(StateCode.SUCCESS);
                        int j = 0;
                        for (int i = 0; i < datalength; i++) {
                            update[j] = packageBytes[module * datalength + i];
                            j++;
                        }
                        responsePackage.setData(update);
                        responsePackage.setCode((short) CrcUtil.CRC16(update, datalength));
                        log.info("success " + ip + ": " + module + " " + StringAndByte.toHexString1(update));
                        ctx.channel().writeAndFlush(responsePackage);
                        module++;
                    } else {
                        responsePackage.setModule(module);
                        responsePackage.setCmd((short) 4369);
                        responsePackage.setStateCode(StateCode.OVER);
                        byte[] last = new byte[length - module * datalength];
                        int j = 0;
                        for (int i = 0; i < length - module * datalength; i++) {
                            last[j] = packageBytes[module * datalength + i];
                            j++;
                        }
                        responsePackage.setData(last);
                        responsePackage.setCode((short) CrcUtil.CRC16(last, length - module * datalength));
                        log.info("success " + ip + ": " + module + " " + StringAndByte.toHexString1(last));
                        ctx.channel().writeAndFlush(responsePackage);
                        log.info("【主动升级】");
                        module = 0;
                        //将升级结果存入缓存
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        FileUploadController fileController = SpringUtil.getBean(FileUploadController.class);
                        RedisTemplate redisTemplate = fileController.redisTemplate;
                        ValueOperations ops = redisTemplate.opsForValue();
                        if( typeNum.equals(gwHexTypeNum)) {
                            Map<String, String> gateway_success_map = (HashMap<String, String>) ops.get("gateway_success_map");
                            if(gateway_success_map == null) {
                                gateway_success_map = new HashMap<String, String>();
                            }
                            gateway_success_map.put(id,sdf.format(new Date()));
                            ops.set("gateway_success_map",gateway_success_map);
                        }
                        if( typeNum.equals(lockHexTypeNum)) {
                            Map<String, String> lock_success_map = (HashMap<String, String>) ops.get("lock_success_map");
                            if(lock_success_map == null) {
                                lock_success_map = new HashMap<String, String>();
                            }
                            lock_success_map.put(id, sdf.format(new Date()));
                            ops.set("lock_success_map",lock_success_map);
                        }
                    }
                    /**
                     * 硬件得到错误的包的返回处理
                     */
                } else if (message.getCmd() == 2) {
                    log.info("fail " + ip + " " + module + " " + StringAndByte.toHexString1(message.getData()));
                    ctx.channel().writeAndFlush(responsePackage);
                    log.info("fail " + ip + ": " + module + " " + StringAndByte.toHexString1(responsePackage.getData()));
                }
            }
        } else {
            log.error("【报文错误】接受的request格式错误");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("【TCP】" + cause.toString());
    }
}
