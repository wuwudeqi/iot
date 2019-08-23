package com.ekoplat.iot.controller;

import com.ekoplat.iot.dataobject.GatewayAndLock;
import com.ekoplat.iot.service.GatewayAndLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 10:17 2019-08-23
 **/
@Controller
public class GwLockController {

    @Autowired
    private GatewayAndLockService gatewayAndLockService;

    @GetMapping("getAllDevice")
    @ResponseBody
    public List<GatewayAndLock> getAllDevice(){
        return  gatewayAndLockService.findAll();
    }

    @GetMapping("getDeviceById")
    @ResponseBody
    public List<GatewayAndLock> getDeviceById(String id){
        List list = new ArrayList<>();
        GatewayAndLock gw;
        gw  = gatewayAndLockService.findBygwId(id);
        if(gw == null) {
            gw = gatewayAndLockService.findByLockId(id);
        }
        list.add(gw);
        return list;
    }
}
