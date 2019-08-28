package com.ekoplat.iot.controller;

import com.ekoplat.iot.dataobject.PackageInfo;
import com.ekoplat.iot.repository.PackageRepository;
import com.ekoplat.iot.server.NettyServer;
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

    @GetMapping("activeUpdateByLocal")
    public String activeUpdateByLocal() {
        return "activeUpdateByLocal";
    }

    @GetMapping("getLastVersion")
    @ResponseBody

    public Map<String, Object> getLastVersion() {
        Map<String, Object> map = new HashMap<String, Object>();
        PackageInfo lastGatewayPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("gateway");
        PackageInfo lastLockPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("lock");
        if (lastGatewayPackage == null) {
            lastGatewayPackage = new PackageInfo();
        }
        if( lastLockPackage == null) {
            lastLockPackage = new PackageInfo();
        }
        map.put("lastGatewayVersion", lastGatewayPackage.getVersion());
        map.put("lastLockVersion", lastLockPackage.getVersion());
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
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String fileName : fileNames) {
            String[] ts = fileName.split("_v");
            String[] split = ts[1].split("\\.");
            List list = map.get("gateway");
            if (list == null) {
                list = new ArrayList<String>();
            }
            list.add(split[0] + "." + split[1]);
            map.put(ts[0], list);
        }
        for (String filename : fileNames) {
            File file = new File(filePath + filename);
            if (file.exists()) {
                file.delete();
            }
            log.info("【升级包】选择{}升级包服务器删除成功",filename);
        }
        for (String typeName : map.keySet()) {
            for (String version : map.get(typeName)) {
                packageRepository.deleteByTypeNameAndVersion(typeName, version);
                log.info("【升级包】选择{},{}升级包数据库删除成功",typeName,version);
            }
        }
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
        //断开所有连接
        NettyServer.destroy();
        //将全部的网关状态设置为0
        GatewayAndLockService gatewayAndLockService = SpringUtil.getBean(GatewayAndLockService.class);
        gatewayAndLockService.changeAllGwStatus();
        return "所有设备下线成功，请重新部署服务器";
    }

}
