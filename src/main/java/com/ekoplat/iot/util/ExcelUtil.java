package com.ekoplat.iot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ekoplat.iot.model.UpdateInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 16:31 2019-07-31
 **/
@Slf4j
public class ExcelUtil {

    public static List<String> readExcelFile(InputStream inputStream, String fileName) {

        /**
         * 这个inputStream文件可以来源于本地文件的流，
         *  也可以来源与上传上来的文件的流，也就是MultipartFile的流，
         *  使用getInputStream()方法进行获取。
         */


        /**
         * 然后再读取文件的时候，应该excel文件的后缀名在不同的版本中对应的解析类不一样
         * 要对fileName进行后缀的解析
         */
        List<String> list = new ArrayList<String>();
        Workbook workbook = null;
        try {
            //判断什么类型文件
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (workbook == null) {
            return list;
        } else {
            //获取所有的工作表的的数量
            int numOfSheet = workbook.getNumberOfSheets();
            //遍历表
            for (int i = 0; i < numOfSheet; i++) {
                //获取一个sheet也就是一个工作本。
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null) continue;
                //获取一个sheet有多少Row
                int lastRowNum = sheet.getLastRowNum();
                if (lastRowNum == 0) continue;
                Row row;
                for (int j = 1; j <= lastRowNum; j++) {
                    row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    //获取一个Row有多少Cell
                    UpdateInfo updateInfo = new UpdateInfo();
                    short lastCellNum = row.getLastCellNum();
                    for (int k = 0; k <= lastCellNum; k++) {
                        if (row.getCell(k) == null) {
                            continue;
                        }
                        row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
                        String res = row.getCell(k).getStringCellValue().trim();
                        list.add(res);
                    }
                }
            }
        }
        return list;
    }

    public static void writeExcel(String flag, Map<String, String> map, OutputStream outputStream) {
        /**
         * 这个outputstream可以来自与文件的输出流，
         * 也可以直接输出到response的getOutputStream()里面
         * 然后用户就可以直接解析到你生产的excel文件了
         */
        //Workbook wb = new SXSSFWorkbook(100);
        Workbook wb = new XSSFWorkbook();
        //创建一个工作本
        Sheet sheet = wb.createSheet("sheet");

        //通过一个sheet创建一个Row
        Row row0 = sheet.createRow(0);

        if (flag.equals("send_success")) {
            Cell cell00 = row0.createCell(0);
            Cell cell01 = row0.createCell(1);
            Cell cell02 = row0.createCell(2);
            Cell cell03 = row0.createCell(3);
            Cell cell04 = row0.createCell(4);
            cell00.setCellValue("gateway");
            cell01.setCellValue("lock");
            cell02.setCellValue("ip");
            cell03.setCellValue("msg");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell04.setCellValue( sdf.format(new Date()));
            int i = 1;
            for (String ip : map.keySet()) {
                String jsonStr = map.get(ip);
                JSONObject json = JSON.parseObject(jsonStr);
                //通过row创建一个cell
                Row row = sheet.createRow(i);
                Cell cell0 = row.createCell(0);
                Cell cell1 = row.createCell(1);
                Cell cell2 = row.createCell(2);
                Cell cell3 = row.createCell(3);
                cell2.setCellValue(ip);
                cell3.setCellValue(jsonStr);
                cell0.setCellValue(json.getString("gwId"));
                cell1.setCellValue(json.getString("lockId"));
                i++;
            }
        } else if (flag.equals("gateway_send_fail")||flag.equals("lock_send_fail")){
            Cell cell00 = row0.createCell(0);
            Cell cell01 = row0.createCell(1);
            Cell cell02 = row0.createCell(2);
            cell00.setCellValue(flag);
            cell01.setCellValue("msg");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell02.setCellValue( sdf.format(new Date()));
            int i = 1;
            for (String id : map.keySet()) {
                Row row = sheet.createRow(i);
                Cell cell0 = row.createCell(0);
                Cell cell1 = row.createCell(1);
                cell0.setCellValue(id);
                cell1.setCellValue(map.get(id));
                i++;
            }
        } else if (flag.equals("gateway_update_success") || flag.equals("lock_update_success")) {
            Cell cell00 = row0.createCell(0);
            Cell cell01 = row0.createCell(1);
            Cell cell02 = row0.createCell(2);
            cell00.setCellValue(flag);
            cell01.setCellValue("msg");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell02.setCellValue( sdf.format(new Date()));
            int i = 1;
            for (String id : map.keySet()) {
                Row row = sheet.createRow(i);
                Cell cell0 = row.createCell(0);
                Cell cell1 = row.createCell(1);
                cell0.setCellValue(id);
                cell1.setCellValue(map.get(id));
                i++;
            }
        }

        try {
            wb.write(outputStream);
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
