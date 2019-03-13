package com.wechat4j.wechat;

import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
    }

    @Bean
    public WebWeixinApi webWeixinApi(){
        return new WebWeixinApi();
    }

    @Bean
    public Wechat wechat(WebWeixinApi webWeixinApi, ReceivedMsgHandler receivedMsgHandler){
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(webWeixinApi);
        wechat.addReceivedMsgHandler(receivedMsgHandler);
        return wechat;
    }

}
