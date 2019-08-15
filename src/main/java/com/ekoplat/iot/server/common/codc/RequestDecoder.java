package com.ekoplat.iot.server.common.codc;


import com.ekoplat.iot.server.common.constant.Head;
import com.ekoplat.iot.server.common.model.RequestCmd;
import com.ekoplat.iot.util.TeaUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RequestDecoder extends ByteToMessageDecoder {


    /**
     * 数据包基本长度
     */
    public static int BASE_LENTH = 4 + 2 + 2 + 4;


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {

        int beginReader = buffer.readerIndex();


        buffer.markReaderIndex();
        int a = buffer.readInt();
        //进入升级
        if (a != Head.FLAG) {
            buffer.readerIndex(beginReader);
            buffer.readerIndex(beginReader);
            //吸收 01 00
            buffer.readShort();
            byte[] b = new byte[buffer.readableBytes()];
            //复制内容到字节数组b
            buffer.readBytes(b);
            byte[] encryptBytes = new byte[256];
            //去掉末尾 00 00
            System.arraycopy(b, 0, encryptBytes, 0, 256);
            int[] encryptInts = new int[256];
            for (int i = 0; i < 256; i++) {
                encryptInts[i] = encryptBytes[i] & 0xff;
            }
            //将int数组转换long数组
            long[] encryptLongs = TeaUtil.int2long(encryptInts);
            //解密long
            long[] decrpyt = TeaUtil.decrpyt(encryptLongs);
            //将long类型分解成实际byte
            byte[] decrpytBytes = TeaUtil.long2byte(decrpyt);

            byte[] head = new byte[4];
            System.arraycopy(decrpytBytes, 0, head, 0, 4);
            if (TeaUtil.getLong(head) != Head.FLAG) {
                String str = new String(decrpytBytes);
                out.add(str);
            } else {
                byte[] modelBytes = new byte[2];
                byte[] cmdBytes = new byte[2];
                byte[] dataLengthBytes = new byte[4];
                System.arraycopy(decrpytBytes, 4, modelBytes, 0, 2);
                System.arraycopy(decrpytBytes, 6, cmdBytes, 0, 2);
                System.arraycopy(decrpytBytes, 8, dataLengthBytes, 0, 4);
                long dataLength = TeaUtil.getLong(dataLengthBytes);
                byte[] data = new byte[(int) dataLength];
                System.arraycopy(decrpytBytes, 12, data, 0, (int) dataLength);

                RequestCmd requestCmd = new RequestCmd();
                requestCmd.setModule((short) TeaUtil.getLong(modelBytes));
                requestCmd.setCmd((short) TeaUtil.getLong(cmdBytes));
                requestCmd.setData(data);
                out.add(requestCmd);
            }
        } else {
            buffer.readerIndex(beginReader);

            //可读长度必须大于基本长度
            if (buffer.readableBytes() >= BASE_LENTH) {
                //防止socket字节流攻击
                if (buffer.readableBytes() > 2048) {
                    buffer.skipBytes(buffer.readableBytes());
                }

                //吃掉包头
                buffer.readInt();


                //模块号
                short module = buffer.readShort();
                //命令号
                short cmd = buffer.readShort();
                //长度
                int length = buffer.readInt();


                //读取data数据
                byte[] data = new byte[length];
                buffer.readBytes(data);
                RequestCmd requestCmd = new RequestCmd();
                requestCmd.setModule(module);
                requestCmd.setCmd(cmd);
                requestCmd.setData(data);

                //继续往下传递
                out.add(requestCmd);
                RequestCmd request = new RequestCmd();
                request.setModule(module);
                request.setCmd(cmd);
                request.setData(data);

                //继续往下传递
                out.add(request);

            }
        }

    }
}