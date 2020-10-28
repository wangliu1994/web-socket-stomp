package com.winine.websocketstomp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author winnie [wangliu023@qq.com]
 * @date 2020/10/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketMessage {

    private String name;

    private String title;

    private Long time;
}
