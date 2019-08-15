package com.ekoplat.iot.server.common.codc;

import com.ekoplat.iot.server.common.model.ResponseCmd;
import com.ekoplat.iot.server.common.model.ResponsePackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * 请求编码器
 * <pre>
 * 数据包格式
 * +——----——+——-----——+——----——+——----——+——-----——+——-----——+
 * | 包头          | 模块号        | 命令号       |  状态码    |  长度          |   数据       |
 * +——----——+——-----——+——----——+——----——+——-----——+——-----——+
 * </pre>
 * 包头4字节
 * 模块号2字节short
 * 命令号2字节short
 * 长度4字节(描述数据部分字节长度)
 * <<<<<<< Updated upstream
 *
 * @author -琴兽-
 */
public class ResponseEncoder extends MessageToByteEncoder {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {

        if (o instanceof ResponsePackage) {
            ResponsePackage responsePackage = (ResponsePackage) (o);

            //包头
            out.writeInt(responsePackage.getHEAD());
            //module
            out.writeInt(responsePackage.getModule());
            //cmd
            out.writeShort(responsePackage.getCmd());
            //状态码
            out.writeInt(responsePackage.getStateCode());
            //发送包数
            out.writeInt(responsePackage.getPackageNum());
            //长度

            out.writeInt(responsePackage.getDataLength());
            //data
            if (responsePackage.getData() != null) {
                out.writeBytes(responsePackage.getData());
            }
            //校验码
            out.writeShort(responsePackage.getCode());
        } else if (o instanceof ResponseCmd) {
            ResponseCmd responseCmd = (ResponseCmd) o;
//            byte[] headBytes = new byte[]{
//                    0x11, 0x22, 0x33, 0x44
//            };
//
//            short moduleShort = responseCmd.getModule();
//            byte highModuleShort = (byte) (0x00FF & (moduleShort >> 8));//定义第一个byte
//            byte lowModuleShort = (byte) (0x00FF & moduleShort);//定义第二个byte
//            byte[] moduleBytes = new byte[]{
//                    highModuleShort, lowModuleShort
//            };
//            short cmdShort = responseCmd.getCmd();
//            byte highCmdShort = (byte) (0x00FF & (cmdShort >> 8));//定义第一个byte
//            byte lowCmdShort = (byte) (0x00FF & cmdShort);//定义第二个byte
//            byte[] cmdBytes = new byte[]{
//                    highCmdShort, lowCmdShort
//            };
//
//            int lengthInt = responseCmd.getLength();
//            byte firstLength = (byte) (0x000000FF & (lengthInt >> 24));//定义第一个byte
//            byte secondLength = (byte) (0x000000FF & (lengthInt >> 16));//定义第二个byte
//            byte thirdLength = (byte) (0x000000FF & (lengthInt >> 8));//定义第三个byte
//            byte fourthLength = (byte) (0x000000FF & (lengthInt));//定义第四个byte
//            byte[] lengthBytes = new byte[]{
//                    firstLength, secondLength, thirdLength, fourthLength
//            };
//            byte[] data = responseCmd.getData();
//
//            byte[] originalBytes = new byte[256];
//            System.arraycopy(headBytes, 0, originalBytes, 0, 4);
//            System.arraycopy(moduleBytes, 0, originalBytes, 4, 2);
//            System.arraycopy(cmdBytes, 0, originalBytes, 6, 2);
//            System.arraycopy(lengthBytes, 0, originalBytes, 8, 4);
//            System.arraycopy(data, 0, originalBytes, 12, data.length);
//
//            long[] longs = TeaUtil.byte2long(originalBytes);
//            long[] encrypt = TeaUtil.encrypt(longs);
//            int[] sendIntes = TeaUtil.long2int(encrypt);
//            byte[] sendBytes = TeaUtil.enlong2byte(encrypt);
//            byte[] headLengthBytes = new byte[]{
//                    0x01, 0x00
//            };
//            byte[] tailLengthBytes = new byte[]{
//                    0x00, 0x00
//            };
//            out.writeBytes(headLengthBytes);
//            out.writeBytes(sendBytes);
//            out.writeBytes(tailLengthBytes);
            //包头
            out.writeInt(responseCmd.getHEAD());
            //module
            out.writeShort(responseCmd.getModule());
            //cmd
            out.writeShort(responseCmd.getCmd());
            //长度
            out.writeInt(responseCmd.getLength());
            //data
            out.writeBytes(responseCmd.getData());
        } else if (o instanceof String) {
            out.writeBytes(o.toString().getBytes());
        }
    }
}

