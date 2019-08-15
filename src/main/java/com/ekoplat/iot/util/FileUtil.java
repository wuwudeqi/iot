package com.ekoplat.iot.util;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {

	/** 
     * 获得指定文件的byte数组 
     */  
    public static byte[] getBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);
//            if(!FileBytes.exists()){
//                //先得到文件的上级目录，并创建上级目录，在创建文件
//                FileBytes.getParentFile().mkdir();
//                try {
//                    //创建文件
//                    FileBytes.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }
    
    /** 
     * 根据byte数组，生成文件 
     */  
    public static void getFile(byte[] bfile, String filePath,String fileName) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
                dir.mkdirs();  
            }  
            file = new File(filePath+"\\"+fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }

    /**
     * 方法：readTxt
     * 功能：读取txt文件并把txt文件的内容---每一行作为一个字符串加入到List中去
     * 参数：txt文件的地址
     * 返回:Map
     * @param file
     * @return
     * @throws IOException
     */
    public static Map<String, List<String>> readTxt(String file) throws IOException {

        Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
        List<String> allLines = Files.readAllLines(Paths.get(file));

        //以下为我截取文件内容，一行分成2段，第一段设置为Map的Key，第二段设置为Map的Value
        for (String line : allLines) {
            if (line != "") {
                ArrayList<String> list = new ArrayList<>();
                String[] temp = line.split(" ");
                list.add(temp[1]);
                list.add(temp[2]);
                tempMap.put(temp[0],list);
            }
        }

        return tempMap;
    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    public static String generateFileName (String name, String version) {
        return  name + "_v" + version + ".bin";
    }

    /*
    从文件名得到版本号 例如lock_v3.1.bin
     */
    public static byte[] getVersionByFileName(String originalFileName) {
        String prefix = originalFileName.substring(originalFileName.lastIndexOf("."));
        int num=prefix.length();//得到后缀名长度
        String fileName=originalFileName.substring(0, originalFileName.length()-num);//得到文件名。去掉了后缀

        String[] vs1 = fileName.split("_v");
        String type = vs1[0];
        String version = vs1[1];
        String[] versionNum = version.split("\\.");
        int bigVersion = Integer.valueOf(versionNum[0]);
        int littleVersion = Integer.valueOf(versionNum[1]);
        byte[] version_int= new byte[]{
                (byte) bigVersion, (byte) littleVersion
        };
        return  version_int;
    }

    public static String getTypeNameByFileName(String originalFileName) {
        String[] vs = originalFileName.split("_v");
        return vs[0];
    }


}
