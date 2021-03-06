package com.winine.websocketstomp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * 参考文档 https://zhuanlan.zhihu.com/p/58311007
 *
 * @author winnie [wangliu023@qq.com]
 * @date 2020/10/28
 */
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 使用 ws://192.168.106.107:8080/endpointWinnie/655/hoxr3tzx/websocket连接
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //注册一个 Stomp 的节点(endpoint),并指定使用 SockJS 协议。
        //.setAllowedOrigins("*")解决跨域问题
        registry.addEndpoint("/endpointWinnie").setAllowedOrigins("*")
                .withSockJS()
                .setInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        log.info("beforeHandshake: getRemoteAddress--------------" + request.getRemoteAddress());
                        attributes.put("remoteAddress", request.getRemoteAddress());
                        attributes.put("remoteUri", request.getURI());
                        attributes.put("sessionId", ((ServletServerHttpRequest) request).getServletRequest().getSession().getId());
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
                        try {
                            log.info("beforeHandshake: getRemoteAddress--------------" + response.getBody().toString());
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                        super.afterHandshake(request, response, wsHandler, ex);
                    }
                });
    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 广播式配置名为 /topic 消息代理
        registry.enableSimpleBroker("/topic", "/user");
        registry.setApplicationDestinationPrefixes("/topic");
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 输入通道参数设置
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        //设置消息输入通道的线程池线程数
        registration.taskExecutor().corePoolSize(4)
                //最大线程数
                .maxPoolSize(8)
                //线程活动时间
                .keepAliveSeconds(60);
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                Map nativeHeadersMap = (Map) message.getHeaders().get("nativeHeaders");
                if (nativeHeadersMap != null) {
                    log.info(nativeHeadersMap.toString());
                }
                StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
                String remoteAddress = "unknown";
                if (sha.getSessionAttributes().get("remoteAddress") != null) {
                    remoteAddress = sha.getSessionAttributes().get("remoteAddress").toString();
                }
                String query = sha.getSessionAttributes().get("remoteUri").toString();
                String sessionId = sha.getSessionAttributes().get("sessionId").toString();

                //判断客户端的连接状态
                switch (sha.getCommand()) {
                    case CONNECT:
                        log.info("客户端: {} 连接成功, uri = {}, sessionId = {}", remoteAddress, query, sessionId);
                        break;
                    case CONNECTED:
                        break;
                    case DISCONNECT:
                        log.info("客户端: {} 断开连接, uri = {}, sessionId = {}", remoteAddress, query, sessionId);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 输出通道参数设置
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                byte[] bytes = (byte[]) message.getPayload();
                if (bytes != null) {
                    String msg = new String(bytes);
                    log.info(msg);
                }
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                byte[] bytes = (byte[]) message.getPayload();
                if (bytes != null) {
                    String msg = new String(bytes);
                    log.info(msg);
                }
            }
        });
    }
}
