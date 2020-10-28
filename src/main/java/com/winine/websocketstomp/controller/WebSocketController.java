package com.winine.websocketstomp.controller;

import com.alibaba.fastjson.JSON;
import com.winine.websocketstomp.SocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : winnie [wangliu023@qq.com]
 * @date : 2020/6/30
 * @desc
 */
@RestController
@RequestMapping("/webSocket")
@Slf4j
public class WebSocketController {

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 连接
     *["CONNECT\nuserId:1\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\u0000"]
     *
     * 订阅
     * ["SUBSCRIBE\nid:sub-0\ndestination:/topic/---id----\n\n\u0000"]
     */
    @GetMapping("/test")
    public void sendMessage1(@RequestParam("id") String id) {
        SocketMessage message = new SocketMessage("winnie", "hello", System.currentTimeMillis());
        String path = "/topic";
        if (id != null) {
            path += "/" + id;
        }
        simpMessagingTemplate.convertAndSend(path, message);
        log.info("推送消息路径：/{}，message:{}", path, JSON.toJSONString(message));
    }
}
