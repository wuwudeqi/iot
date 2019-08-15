package com.ekoplat.iot.util;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 10:44 2019-07-08
 **/

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nickwong on 31/07/2018.
 * 根据1-8楼的建议，优化了代码
 */
public class IOTxtUTtil {

    /**
     * 读入TXT文件
     */
    public static List readFile(String pathname) {
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        List list = new ArrayList<>();
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 写入TXT文件
     */
    public static void writeFile(Map<String,List<String>> updateMap, String pathname) {
        try {
            File writeName = new File(pathname); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                for(String key : updateMap.keySet()){
                    List<String> nameAndVersion = updateMap.get(key);
                    out.write(nameAndVersion.get(0)+" ");
                    out.write(nameAndVersion.get(1)+"\r\n");
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


