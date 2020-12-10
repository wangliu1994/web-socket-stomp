package com.winine.websocketstomp.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @author winnie [wangliu023@qq.com]
 * @date 2020/12/10
 */
@Controller
@Slf4j
public class TestSendController {

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 连接
     *  ["CONNECT\nuserId:1\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\u0000"]
     *
     * 订阅
     *  ["SUBSCRIBE\nid:sub-0\ndestination:/topic/test\n\n\u0000"]
     *
     * 发送
     *  ["SEND\ndestination:/topic/message\ncontent-length:22\n\n{\"name\":\"winnie-test\"}\u0000"]
     */
    @MessageMapping("/message")
    @SendTo("/topic/test")
    public String message(String message) throws InterruptedException {
        // simulated delay
        String res = putMessage(message);
        log.info(res);
        return res;
    }


    private String putMessage(Object obj) throws InterruptedException {
        // simulated delay
        Thread.sleep(1000);
        return JSON.toJSONString(obj);
    }


    @MessageMapping("/message1")
    @SendTo("/topic/test")
    public String message1(Object message) throws InterruptedException {
        // simulated delay
        Thread.sleep(1000);
        String res = JSON.toJSONString(message);
        log.info(res);
        return res;
    }
}
