package com.openluat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeadController {
    @RequestMapping(path = "/",method = RequestMethod.HEAD)
    public void headTest(){

    }

}
