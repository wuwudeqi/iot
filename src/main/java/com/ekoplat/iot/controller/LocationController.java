package com.ekoplat.iot.controller;

import com.ekoplat.iot.dataobject.PackageInfo;
import com.ekoplat.iot.repository.PackageRepository;
import com.ekoplat.iot.service.GatewayAndLockService;
import com.ekoplat.iot.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 11:17 2019-07-31
 **/
@Controller
@Slf4j
public class LocationController {

    @Value("${filePath}")
    public String filePath;

    @Autowired
    private PackageRepository packageRepository;

    @GetMapping("sdk")
    public String sdk() {
        return "sdk";
    }

    @GetMapping("getLastVersion")
    @ResponseBody
    public Map<String,Object> getLastVersion() {
        Map<String, Object> map = new HashMap<String, Object>();
        PackageInfo lastGatewayPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("gateway");
        PackageInfo lastLockPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("lock");
        map.put("lastGatewayVersion",lastGatewayPackage.getVersion());
        map.put("lastLockVersion",lastLockPackage.getVersion());
        return map;
    }

    @GetMapping("getAllPackage")
    @ResponseBody
    public List getAllPackage() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<PackageInfo> packageInfoList = packageRepository.findAll(sort);
        System.out.println(packageInfoList);
        return packageInfoList;
    }

    @PostMapping("deletePackage")
    @ResponseBody
    public Integer deletePackage(String[] fileNames) {
        List<String> versionList = new ArrayList<String>();
        for (String fileName : fileNames) {
            String[] ts = fileName.split("_v");
            String[] split = ts[1].split("\\.");
            versionList.add(split[0] + "." +split[1]);
        }
        for (String filename : fileNames) {
            File file = new File(filePath+filename);
            if (file.exists()) {
                file.delete();
            }

        }
        log.info("【升级包】选择升级包服务器删除成功");
        for (String version : versionList) {
            packageRepository.deleteByVersion(version);
        }
        log.info("【升级包】选择升级包数据库删除成功");
        return fileNames.length;
    }

    @GetMapping("activeUpdate")
    public ModelAndView activeUpdate() {
        ModelAndView view = new ModelAndView();
        view.setViewName("activeUpdate");
        PackageInfo lastGatewayPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("gateway");
        PackageInfo lastLockPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("lock");
        view.addObject("lastGatewayVersion", lastGatewayPackage.getVersion());
        view.addObject("lastLockVersion", lastLockPackage.getVersion());
        return view;
    }

    @GetMapping("excelResult")
    public String Download() {
        return "excelResult";
    }

    @GetMapping("offLineAll")
    @ResponseBody
    public String offLineAll() {
        //将全部的网关状态设置为0
        GatewayAndLockService gatewayAndLockService = SpringUtil.getBean(GatewayAndLockService.class);
        gatewayAndLockService.changeAllGwStatus();
        return "所有设备下线成功，请重新部署服务器";
    }

}
