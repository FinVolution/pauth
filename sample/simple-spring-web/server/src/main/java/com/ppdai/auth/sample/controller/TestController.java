package com.ppdai.auth.sample.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Please add description here.
 *
 */
@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {
    @RequestMapping(value = "/fetch", method = RequestMethod.GET)
    public Response getTest() {
        return Response.mark(MessageType.SUCCESS, "test");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response updateTest(@RequestParam(value = "newData") String newData) {
        return Response.mark(MessageType.SUCCESS, newData);
    }
}
