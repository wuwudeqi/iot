package com.ekoplat.iot.controller;

import com.ekoplat.iot.dataobject.PackageInfo;
import com.ekoplat.iot.repository.PackageRepository;
import com.ekoplat.iot.service.GatewayAndLockService;
import com.ekoplat.iot.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 11:17 2019-07-31
 **/
@Controller
@Slf4j
public class LocationController {

    @Autowired
    private PackageRepository packageRepository;

    @GetMapping("sdk")
    public ModelAndView sdk() {
        ModelAndView view = new ModelAndView();
        view.setViewName("sdk");
        PackageInfo lastGatewayPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("gateway");
        PackageInfo lastLockPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("lock");
        view.addObject("lastGatewayVersion",lastGatewayPackage.getVersion());
        view.addObject("lastLockVersion",lastLockPackage.getVersion());
        return view;
    }

    @GetMapping("activeUpdate")
    public ModelAndView activeUpdate() {
        ModelAndView view = new ModelAndView();
        view.setViewName("activeUpdate");
        PackageInfo lastGatewayPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("gateway");
        PackageInfo lastLockPackage = packageRepository.findFirstBytypeNameOrderByIdDesc("lock");
        view.addObject("lastGatewayVersion",lastGatewayPackage.getVersion());
        view.addObject("lastLockVersion",lastLockPackage.getVersion());
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
