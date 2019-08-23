package com.ekoplat.iot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ekoplat.iot.dataobject.GatewayAndLock;
import com.ekoplat.iot.dataobject.PackageInfo;
import com.ekoplat.iot.repository.PackageRepository;
import com.ekoplat.iot.service.GatewayAndLockService;
import com.ekoplat.iot.util.CmdUtil;
import com.ekoplat.iot.util.ExcelUtil;
import com.ekoplat.iot.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传的Controller
 *
 * @author 单红宇(CSDN CATOOP)
 * @create 2017年3月11日
 */
@Controller
@Slf4j
public class FileUploadController {

    @Value("${filePath}")
    public String filePath;

    @Value("${excelPath}")
    public String excelPath;

    @Value("${param.gateway.typeNum}")
    private String gwHexTypeNum;

    @Value("${param.lock.typeNum}")
    private String lockHexTypeNum;

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private GatewayAndLockService gatewayAndLockService;


    /**
     * 文件上传具体实现方法（单文件上传）
     *
     * @param file
     * @return
     * @author 单红宇(CSDN CATOOP)
     * @create 2017年3月11日
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, @Param("typeName") String typeName, @Param("version") String version) {
        String updateType = "passivity";
        String[] checkUpdateType = typeName.split("/");
        if (checkUpdateType.length > 1) {
            updateType = "active";
        }
        typeName = checkUpdateType[0];
        if (!file.isEmpty()) {
            try {
                PackageInfo lastPackageInfo = packageRepository.findFirstBytypeNameOrderByIdDesc(typeName);
                if (!(version.compareTo(lastPackageInfo.getVersion()) > 0)) {
                    return "当前服务器" + typeName + "最新版本为" + lastPackageInfo.getVersion() + ",填入的版本为" + version + ",请返回重新选择升级包";
                }
                String fileName = FileUtil.generateFileName(typeName, version);
                String path = filePath + fileName;
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(path)));
                out.write(file.getBytes());
                PackageInfo packageInfo = new PackageInfo();
                packageInfo.setTypeName(typeName);
                packageInfo.setVersion(version);
                packageInfo.setUpdateType(updateType);
                if (typeName.equals("gateway")) {
                    packageInfo.setTypeNum(gwHexTypeNum);
                }
                if (typeName.equals("lock")) {
                    packageInfo.setTypeNum(lockHexTypeNum);
                }
                packageRepository.save(packageInfo);
                log.info("【文件上传】{}上传成功，path:{}", updateType,path);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                log.info("【文件上传】{}上传失败，error:{}", updateType,e.getMessage());
                return "上传失败," + e.getMessage();
            } catch (IOException e) {
                log.info("【文件上传】{}上传失败，error:{}", updateType,e.getMessage());
                return "上传失败," + e.getMessage();
            }
            return "上传成功";
        } else {
            return "上传失败，因为文件是空的.";
        }
    }


    @RequestMapping("/batchUpdate")
    public ModelAndView batchUpdate(@RequestParam("gwIdExcel") MultipartFile gwIdExcel, @RequestParam("gwIdPackage") MultipartFile gwIdPackage, @RequestParam("lockIdExcel") MultipartFile lockIdExcel, @RequestParam("lockIdPackage") MultipartFile lockIdPackage) throws IOException, DecoderException {
        ModelAndView view = new ModelAndView();
        String gwIdExcelName = gwIdExcel.getOriginalFilename();
        String gwIdFileName = gwIdPackage.getOriginalFilename();
        HashMap<String, String> gwLock_success_Map = new HashMap<>();
        HashMap<String, String> gw_fail_Map = new HashMap<>();
        HashMap<String, String> lock_fail_Map = new HashMap<>();
        if (!"".equals(gwIdFileName) && gwIdFileName != null) {
            //获取网关的版本号
            byte[] gwHexVersion = FileUtil.getVersionByFileName(gwIdFileName);
            String updateVersion = String.valueOf((int) gwHexVersion[0]) + "." + String.valueOf((int) gwHexVersion[1]);
            String typeName = FileUtil.getTypeNameByFileName(gwIdFileName);
            //或得数据库最新的版本
            PackageInfo lastPackageInfo = packageRepository.findFirstBytypeNameOrderByIdDesc(typeName);
            //将上传的版本号和数据库版本号做比对
            if (!(updateVersion.compareTo(lastPackageInfo.getVersion()) > 0)) {
                view.setViewName("error");
                view.addObject("errorMsg", "当前服务器网关最新版本为" + lastPackageInfo.getVersion() + ",填入的版本为" + updateVersion + ",请返回重新选择升级包");
                return view;
            }

            upload(gwIdPackage, typeName + "/active", updateVersion);
            log.info("升级包上传成功");

            List<String> gwList = ExcelUtil.readExcelFile(gwIdExcel.getInputStream(), gwIdExcelName);
            for (String gwId : gwList) {
                GatewayAndLock gl = gatewayAndLockService.findBygwId(gwId);
                if (gl == null) {
                    gw_fail_Map.put(gwId, "{code:\"fail\", msg:\"网关从未上线\"}");
                    continue;
                } else if (gl.getGwStatus() == 0) {
                    gw_fail_Map.put(gwId, "{code:\"fail\", msg:\"网关下线\"}");
                    continue;
                } else {
                    gwLock_success_Map.put(gl.getIp(), "{code:\"" + gwHexTypeNum + "" + Hex.encodeHexString(gwHexVersion, false) + "\", msg:\"只升级网关\", gwId:\"" + gl.getGwId() + "\", lockId:\"\"}");
                }
            }

        }


        String LockExcelName = lockIdExcel.getOriginalFilename();
        String lockIdFileName = lockIdPackage.getOriginalFilename();

        if (!"".equals(lockIdFileName) && lockIdFileName != null) {
            //获取网关的版本号
            byte[] lockHexVersion = FileUtil.getVersionByFileName(lockIdFileName);
            String updateVersion = String.valueOf((int) lockHexVersion[0]) + "." + String.valueOf((int) lockHexVersion[1]);
            String typeName = FileUtil.getTypeNameByFileName(lockIdFileName);
            //或得数据库最新的版本
            PackageInfo lastPackageInfo = packageRepository.findFirstBytypeNameOrderByIdDesc(typeName);
            //将上传的版本号和数据库版本号做比对
            if (!(updateVersion.compareTo(lastPackageInfo.getVersion()) > 0)) {
                view.setViewName("error");
                view.addObject("errorMsg", "当前服务器锁的最新版本为" + lastPackageInfo.getVersion() + ",填入的版本为" + updateVersion + ",请返回重新选择升级包");
                return view;
            }

            upload(lockIdPackage, typeName + "/active", updateVersion);
            log.info("升级包上传成功");

            List<String> lockList = ExcelUtil.readExcelFile(lockIdExcel.getInputStream(), LockExcelName);
            for (String lockId : lockList) {
                GatewayAndLock gl = gatewayAndLockService.findByLockId(lockId);
                if (gl == null) {
                    lock_fail_Map.put(lockId, "{code:\"fail\", msg:\"锁从未上线\"}");
                    continue;
                } else if (gl.getGwStatus() == 0 || gl.getLockStatus() == 0 || "".equals(gl.getLockId())) {
                    lock_fail_Map.put(lockId, "{code:\"fail\", msg:\"锁下线\"}");
                    continue;
                } else {
                    if (gwLock_success_Map.get(gl.getIp()) == null) {
                        gwLock_success_Map.put(gl.getIp(), "{code:\"" + lockHexTypeNum + "" + Hex.encodeHexString(lockHexVersion, false) + "\", msg:\"只升级锁\", gwId:\"" + gl.getGwId() + "\", lockId:\"" + lockId + "\"}");
                    } else {
                        JSONObject json = JSON.parseObject(gwLock_success_Map.get(gl.getIp()));
                        String newCode = json.getString("code") + lockHexTypeNum + Hex.encodeHexString(lockHexVersion, false);
                        json.put("code", newCode);
                        json.put("msg", "网关和锁同时升级");
                        json.put("lockId", gl.getLockId());
                        gwLock_success_Map.put(gl.getIp(), json.toString());
                    }
                }
            }

        }

        CmdUtil.sendActiveUpdateCmd(gwLock_success_Map);


        view.setViewName("excelResult");
        FileOutputStream fileOutputStream = new FileOutputStream(excelPath + "/send_success_result.xlsx");
        ExcelUtil.writeExcel("send_success", gwLock_success_Map, fileOutputStream);
        FileOutputStream fileOutputStream1 = new FileOutputStream(excelPath + "/gateway_send_fail_result.xlsx");
        ExcelUtil.writeExcel("gateway_send_fail", gw_fail_Map, fileOutputStream1);
        FileOutputStream fileOutputStream2 = new FileOutputStream(excelPath + "/lock_send_fail_result.xlsx");
        ExcelUtil.writeExcel("lock_send_fail", lock_fail_Map, fileOutputStream2);
        return view;
    }


    // 文件下载相关代码
    @RequestMapping("downfile/{typeName}")
    @ResponseBody
    public void downloadFile(HttpServletResponse response, @PathVariable String typeName) throws Exception {
        String fileName = typeName + "_result.xlsx";
        if (fileName != null) {
            //设置文件路径
            File file = new File(excelPath + fileName);
            if (file.exists()) {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
                // filePath生成的excel文件存放路径
                FileInputStream fis = new FileInputStream(excelPath + fileName);
                byte[] buffer = new byte[1024];
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    log.info("【文件下载】success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    // 文件下载相关代码
    @RequestMapping("updateSuccess/{typeName}")
    public void updateSuccess(HttpServletResponse response, @PathVariable String typeName) throws Exception {
        ValueOperations ops = redisTemplate.opsForValue();
        if (typeName.equals("gateway_update_success")) {
            Map<String, String> gateway_success_map = (Map<String, String>) ops.get("gateway_success_map");
            if (gateway_success_map != null) {
                FileOutputStream fileOutputStream1 = new FileOutputStream(excelPath + "/gateway_update_success_result.xlsx");
                ExcelUtil.writeExcel("gateway_update_success", gateway_success_map, fileOutputStream1);
                redisTemplate.delete("gateway_success_map");
            }
        }
        if (typeName.equals("lock_update_success")) {
            Map<String, String> lock_success_map = (Map<String, String>) ops.get("lock_success_map");
            if (lock_success_map != null) {
                FileOutputStream fileOutputStream2 = new FileOutputStream(excelPath + "/lock_update_success_result.xlsx");
                ExcelUtil.writeExcel("lock_update_success", lock_success_map, fileOutputStream2);
                redisTemplate.delete("lock_success_map");
            }
        }

        String fileName = typeName + "_result.xlsx";
        File file = new File(excelPath + fileName);
        if (file.exists()) {
            downloadFile(response, typeName);
        }
    }


}