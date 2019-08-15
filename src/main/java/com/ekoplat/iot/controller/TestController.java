package com.ekoplat.iot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 09:51 2019-08-08
 **/
@Controller
public class TestController {

    @RequestMapping("test")
    @ResponseBody
    public String test1() {
        return "suuccess";
    }
}
