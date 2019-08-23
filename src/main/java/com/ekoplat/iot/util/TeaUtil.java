package com.ekoplat.iot.util;

import com.ekoplat.iot.server.common.constant.Head;
import com.ekoplat.iot.server.common.model.ResponseCmd;

/**
 * Tea算法
 * 每次操作可以处理8个字节数据
 * KEY为64字节,应为包含4个int型数的int[]，一个int为4个字节
 * 加密解密轮数应为8的倍数，推荐加密轮数为64轮
 */
public class TeaUtil {


    private final static long[] KEY = new long[]{//加密解密所用的KEY
            2773841067L, 4270263649L,
            1910514732L, 3016725768L
    };

    private static int BLOCK_SIZE = 256;
    private static int S_LOOPTIME = 1;
    private static long DELTA = 2320203537L;

    //加密
    public static long[] encrypt(long[] buf) {//
        long[] v = buf;
        int n = BLOCK_SIZE / 4;
        long[] k = KEY;
        long z = v[n - 1], y = v[0], sum = 0, e;
        int p = 0, q = 0;
        // Coding Part
        q = S_LOOPTIME + 52 / n;
        while (q-- > 0) {
            sum = (sum + DELTA) & 0xffffffffL;
            e = sum >> 2 & 3;
            for (p = 0; p < n - 1; p++) {
                y = v[p + 1];
                long test = ((z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)) & 0xffffffffL ^ ((sum ^ y) + (k[(int) (p & 3 ^ e)] ^ z)) & 0xffffffffL;
                v[p] = (v[p] + (((z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)) & 0xffffffffL ^ ((sum ^ y) + (k[(int) (p & 3 ^ e)] ^ z)))) & 0xffffffffL;
                z = v[p];
            }
            y = v[0];
            v[n - 1] = (v[n - 1] + (((z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)) & 0xffffffffL ^ ((sum ^ y) + (k[(int) (p & 3 ^ e)] ^ z)))) & 0xffffffffL;
            z = v[n - 1];
        }
        return v;
    }

    //解密
    public static long[] decrpyt(long[] buf) {
        long[] v = buf;
        int n = BLOCK_SIZE / 4;
        long[] k = KEY;
        long z = v[n - 1], y = v[0], sum = 0, e;
        int p = 0, q = 0;

        q = S_LOOPTIME + 52 / n;
        sum = q * DELTA;
        while (sum != 0) {
            e = sum >> 2 & 3;
            for (p = n - 1; p > 0; p--) {
                z = v[p - 1];
                v[p] = (v[p] - (((z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)) & 0xffffffffL ^ ((sum ^ y) + (k[(int) (p & 3 ^ e)] ^ z)))) & 0xffffffffL;
                y = v[p];
            }
            z = v[n - 1];
            v[0] = (v[0] - (((z >> 5 ^ y << 2) + (y >> 3 ^ z << 4)) & 0xffffffffL ^ ((sum ^ y) + (k[(int) (p & 3 ^ e)] ^ z)))) & 0xffffffffL;
            y = v[0];
            sum -= DELTA;
        }
        return v;
    }


    /**
     * @param data 数组 多少个byte相组合成一个数字
     */
    public static long getLong(byte[] data) {
        long count = 0;
        for (int i = 0; i < data.length; i++) {
            count <<= 8;
            count |= data[i] & 0xff;
        }
        return count;
    }

    public static long getLong(int[] data) {
        long count = 0;
        for (int i = 0; i < 4; i++) {
            count <<= 8;
            count |= data[i] & 0xff;
        }
        return count;
    }

    // 实现数组元素的翻转
    public static byte[] reverse(byte[] arr) {
        // 遍历数组
        for (int i = 0; i < arr.length / 2; i++) {
            // 交换元素
            byte temp = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = arr[i];
            arr[i] = temp;
        }
        // 返回反转后的结果
        return arr;
    }

    // 实现数组元素的翻转
    public static int[] reverse(int[] arr) {
        // 遍历数组
        for (int i = 0; i < arr.length / 2; i++) {
            // 交换元素
            int temp = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = arr[i];
            arr[i] = temp;
        }
        // 返回反转后的结果
        return arr;
    }

    //加密后分散发送
    public static int[] long2int(long[] encrypt) {
        int[] newData = new int[256];
        int t = 0;
        for (int i = 0; i < encrypt.length; i++) {
            long en = encrypt[i];
            char[] hexNum = Long.toHexString(en).toCharArray();
            int z = 0;
            if (hexNum.length != 8) {
                char[] tempHexNum = hexNum;
                hexNum = new char[8];
                int n = tempHexNum.length;
                for (int e = 0; e < 8 - n; e++) {
                    hexNum[e] = '0';
                }
                System.arraycopy(tempHexNum, 0, hexNum, 8 - n, n);
            }
            for (int k = 7; k > 0; k -= 2) {
                try {
                    String hexOne = hexNum[k - 1] + "" + hexNum[k];
                    int temp = Integer.parseInt(hexOne, 16);
                    newData[z + t] = temp;
                    z++;
                } catch (Throwable e) {
                    System.out.println(k + ":" + i);
                }
            }
            t += 4;
        }
        return newData;
    }

    //加密后分散发送
    public static byte[] enlong2byte(long[] encrypt) {
        byte[] newData = new byte[256];
        int t = 0;
        for (int i = 0; i < encrypt.length; i++) {
            long en = encrypt[i];
            char[] hexNum = Long.toHexString(en).toCharArray();
            int z = 0;
            if (hexNum.length != 8) {
                char[] tempHexNum = hexNum;
                hexNum = new char[8];
                int n = tempHexNum.length;
                for (int e = 0; e < 8 - n; e++) {
                    hexNum[e] = '0';
                }
                System.arraycopy(tempHexNum, 0, hexNum, 8 - n, n);
            }
            for (int k = 7; k > 0; k -= 2) {
                try {
                    String hexOne = hexNum[k - 1] + "" + hexNum[k];
                    int temp = Integer.parseInt(hexOne, 16);
                    newData[z + t] = (byte) temp;
                    z++;
                } catch (Throwable e) {
                    System.out.println(k + ":" + i);
                }
            }
            t += 4;
        }
        return newData;
    }

    public static byte[] long2byte(long[] decrpyt) {
        byte[] newData = new byte[256];
        if (decrpyt[0] != Head.REVERSE_FLAG) {
            int t = 0;
            int temp = 0;
            String hexOne = "00";
            int z = 0;
            for (int i = 0; i < decrpyt.length; i++) {
                long en = decrpyt[i];
                char[] hexNum = Long.toHexString(en).toCharArray();
                if (hexNum.length != 8) {
                    char[] tempHexNum = hexNum;
                    hexNum = new char[8];
                    int n = tempHexNum.length;
                    for (int e = 0; e < 8 - n; e++) {
                        hexNum[e] = '0';
                    }
                    System.arraycopy(tempHexNum, 0, hexNum, 8 - n, n);
                }
                z = 0;
                for (int k = 7; k > 0; k -= 2) {
                    hexOne = hexNum[k - 1] + "" + hexNum[k];
                    if (hexOne.equals("00")) {
                        break;
                    }
                    temp = Integer.parseInt(hexOne, 16);
                    newData[z + t] = (byte) temp;
                    z++;
                }
                if (hexOne.equals("00")) {
                    break;
                }
                t += 4;
            }
            byte[] realBytes = new byte[t + z];
            System.arraycopy(newData, 0, realBytes, 0, t + z);
            return realBytes;
        } else {
            char[] lengthHex = Long.toHexString(decrpyt[2]).toCharArray();
            String str = "";
            if (lengthHex.length != 8) {
                str = "";
                char[] tempHexNum = lengthHex;
                lengthHex = new char[8];
                int n = tempHexNum.length;
                for (int e = 0; e < 8 - n; e++) {
                    lengthHex[e] = '0';
                }
                System.arraycopy(tempHexNum, 0, lengthHex, 8 - n, n);
                byte[] realByte = new byte[4];
            }
            for (int i = 7; i > 0; i -= 2) {
                String tempStr = lengthHex[i - 1] + "" + lengthHex[i];
                str = str + tempStr;
            }

            int dataLength = Integer.parseInt(str,16);
            int ableLength = (dataLength + 12) % 4 == 0 ? (dataLength + 12) / 4 : ((dataLength + 12) / 4 + 1);
            int t = 0;
            int temp = 0;
            String hexOne = "00";
            int z = 0;
            for (int i = 0; i < ableLength; i++) {
                long en = decrpyt[i];
                char[] hexNum = Long.toHexString(en).toCharArray();
                if (hexNum.length != 8) {
                    char[] tempHexNum = hexNum;
                    hexNum = new char[8];
                    int n = tempHexNum.length;
                    for (int e = 0; e < 8 - n; e++) {
                        hexNum[e] = '0';
                    }
                    System.arraycopy(tempHexNum, 0, hexNum, 8 - n, n);
                }
                z = 0;
                for (int k = 7; k > 0; k -= 2) {
                    hexOne = hexNum[k - 1] + "" + hexNum[k];
                    temp = Integer.parseInt(hexOne, 16);
                    newData[z + t] = (byte) temp;
                    z++;
                }
                t += 4;
            }
            byte[] realBytes = new byte[t + z];
            System.arraycopy(newData, 0, realBytes, 0, t + z);
            return realBytes;
        }

    }


    public static long[] byte2long(byte[] bytes) {
        byte[] data = new byte[256];
        byte[] array = bytes;
        System.arraycopy(array, 0, data, 0, array.length);
        long[] resultLong = new long[data.length / 4];
        int j = 0;
        for (int i = 0; i < data.length; i += 4) {
            byte[] flag = new byte[4];
            System.arraycopy(data, i, flag, 0, 4);
            reverse(flag);
            long aLong = getLong(flag);
            resultLong[j] = aLong;
            j++;
        }
        return resultLong;
    }

    public static long[] int2long(int[] ints) {
        int[] data = new int[256];
        int[] array = ints;
        System.arraycopy(array, 0, data, 0, array.length);
        long[] resultLong = new long[data.length / 4];
        int j = 0;
        for (int i = 0; i < data.length; i += 4) {
            int[] flag = new int[4];
            System.arraycopy(data, i, flag, 0, 4);
            reverse(flag);
            long aLong = getLong(flag);
            resultLong[j] = aLong;
            j++;
        }
        return resultLong;
    }

    public static long[] bytesEncrypt(ResponseCmd o) {
        ResponseCmd responseCmd = (ResponseCmd) o;
        byte[] headBytes = new byte[]{
                0x11, 0x22, 0x33, 0x44
        };

        short moduleShort = responseCmd.getModule();
        byte highModuleShort = (byte) (0x00FF & (moduleShort >> 8));//定义第一个byte
        byte lowModuleShort = (byte) (0x00FF & moduleShort);//定义第二个byte
        byte[] moduleBytes = new byte[]{
                highModuleShort, lowModuleShort
        };
        short cmdShort = responseCmd.getCmd();
        byte highCmdShort = (byte) (0x00FF & (cmdShort >> 8));//定义第一个byte
        byte lowCmdShort = (byte) (0x00FF & cmdShort);//定义第二个byte
        byte[] cmdBytes = new byte[]{
                highCmdShort, lowCmdShort
        };

        int lengthInt = responseCmd.getLength();
        byte firstLength = (byte) (0x000000FF & (lengthInt >> 24));//定义第一个byte
        byte secondLength = (byte) (0x000000FF & (lengthInt >> 16));//定义第二个byte
        byte thirdLength = (byte) (0x000000FF & (lengthInt >> 8));//定义第三个byte
        byte fourthLength = (byte) (0x000000FF & (lengthInt));//定义第四个byte
        byte[] lengthBytes = new byte[]{
                firstLength, secondLength, thirdLength, fourthLength
        };
        byte[] data = responseCmd.getData();

        byte[] originalBytes = new byte[256];
        System.arraycopy(headBytes, 0, originalBytes, 0, 4);
        System.arraycopy(moduleBytes, 0, originalBytes, 4, 2);
        System.arraycopy(cmdBytes, 0, originalBytes, 6, 2);
        System.arraycopy(lengthBytes, 0, originalBytes, 8, 4);
        System.arraycopy(data, 0, originalBytes, 12, data.length);

        long[] longs = TeaUtil.byte2long(originalBytes);
        long[] encrypt = TeaUtil.encrypt(longs);
        return  encrypt;
    }

    public static void main1(String[] args) {
        String type0 = "{\"GW-ID\":\"00000000000033\",\"type\":0,\"BEAT\":\"send\"}";
        String str = "{\"GW-ID\":\"00000000000033\",\"type\":30,\"MNC\":0,\"LAC\":22570,\"CI\":60319,version:{\"gateway\":\"3.1\",\"lock\":\"3.1\"}} ";
        byte[] bytes = type0.getBytes();
        long[] resultLong = byte2long(bytes);
        long[] encrypt = encrypt(resultLong);
        //发送的加密
        int[] sendMsg = long2int(encrypt);
        for (int i = 0; i < sendMsg.length; i++) {
            String n = Integer.toHexString(sendMsg[i]);
            if(n.length()== 1) {
                n = '0' + n;
            }
            System.out.printf(n+" ");
        }
        System.out.println("\n");
        long[] longs = int2long(sendMsg);
        long[] decrpyt = decrpyt(longs);
        //收到的数据
        byte[] realBytes = long2byte(decrpyt);
        String s = new String(realBytes);
        System.out.println(s);
    }

    public static void main(String[] args) {
        byte[] bei = new byte[]{
                0x11,0x22,0x33,0x44,0x00,0x01,0x00,0x01,0x00,0x00,0x00,0x04,0x00,0x00,0x00,0x00};
        byte[] orginal1 = new byte[]{
                0x11,0x22,0x33,0x44,0x00,0x02,0x00,0x01,0x00,0x00,0x00,0x04,0x00,0x00,0x00,0x00};
        byte[] orginal = new byte[]{
                0x11,0x22,0x33,0x44,0x00,0x02,0x00,0x03,0x00,0x00,0x00,0x12,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x33,0x33,0x00,0x00,0x03,0x01};
        long[] longs = byte2long(orginal1);
        long[] encrypt = encrypt(longs);
        //发送的加密
        int[] sendMsg = long2int(encrypt);
        for (int i = 0; i < sendMsg.length; i++) {
            String n = Integer.toHexString(sendMsg[i]);
            if(n.length()== 1) {
                n = '0' + n;
            }
            System.out.printf(n + " ");
        }

        System.out.println("\n");
        long[] longss = int2long(sendMsg);
        long[] decrpyt = decrpyt(longss);
        //收到的数据
        byte[] realBytes = long2byte(decrpyt);
        String s = new String(realBytes);
    }
}