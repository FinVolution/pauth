package com.ppdai.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康页面，用于运维监控查看状态
 *
 */
@RestController
public class HealthController {

    @RequestMapping(value = "/hs", method = RequestMethod.GET)
    public ResponseEntity<String> checkHealth() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}

