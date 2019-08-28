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
    public List<GatewayAndLock> getAllDevice() {
        return gatewayAndLockService.findAll();
    }

    @GetMapping("getDeviceById")
    @ResponseBody
    public List<GatewayAndLock> getDeviceById(String str) {
        /**
         * 查询规则
         *  1.单一id查询
         *  2.id范围查询
         *  3.网关锁状态查询
         */
        List list = new ArrayList<>();
        if (str.length() == 14) {
            String id = str;
            GatewayAndLock gw;
            gw = gatewayAndLockService.findBygwId(id);
            if (gw == null) {
                gw = gatewayAndLockService.findByLockId(id);
            }
            list.add(gw);
            return list;
        } else if (str.length() == 4) {
            char[] chars = str.toCharArray();
            Integer status = Integer.valueOf(chars[3]) - 48;
            return gatewayAndLockService.findBygwStatus(status);
        } else if (str.length() == 6) {
            char[] chars = str.toCharArray();
            Integer status = Integer.valueOf(chars[5]) - 48;
            return gatewayAndLockService.findBylockStatus(status);
        } else if (str.length() == 29) {
            char[] chars = str.toCharArray();
            if (chars[2] == '0') {
                String[] split = str.split("-");
                String littleId = split[0];
                String bigId = split[1];
                return gatewayAndLockService.gwIdBetween(littleId,bigId);
            }
        }
        return list;
    }
}
